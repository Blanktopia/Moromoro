@file:UseSerializers(
    ItemStackSerializer::class,
    MaterialSerializer::class,
    EnchantmentSerializer::class,
    ColorSerializer::class,
    UUIDSerializer::class
)

package me.weiwen.moromoro.managers

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.extensions.*
import me.weiwen.moromoro.serializers.*
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.BlockState
import org.bukkit.block.data.MultipleFacing
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemFrame
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import kotlin.IllegalArgumentException
import kotlin.math.max
import kotlin.random.Random

@Serializable
sealed class BlockTemplate {
    abstract fun place(ctx: Context): Boolean

    @SerialName("sit-height")
    abstract val sitHeight: Double?

    abstract val drops: ItemStack?
    abstract val canFortune: Boolean
}

@Serializable
@SerialName("mushroom")
class MushroomBlockTemplate(
    val state: Int,
    val material: Material = Material.BROWN_MUSHROOM_BLOCK,
    @SerialName("sit-height")
    override val sitHeight: Double? = null,
    override val drops: ItemStack? = null,
    @SerialName("can-fortune")
    override val canFortune: Boolean = false
) : BlockTemplate() {
    override fun place(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        val item = ctx.item ?: return false
        
        val placedAgainst = ctx.block ?: return false
        val blockFace = ctx.blockFace ?: return false
        val placedBlock = placedAgainst.getRelative(blockFace)

        val blockState = placedBlock.state.apply {
            type = Material.BROWN_MUSHROOM_BLOCK
            customBlockState = state
        }

        val buildEvent = BlockPlaceEvent(
            placedBlock,
            blockState,
            placedAgainst,
            item.clone().apply { amount = 1 },
            player,
            true,
            EquipmentSlot.HAND
        )
        Bukkit.getPluginManager().callEvent(buildEvent)
        if (buildEvent.isCancelled) {
            return false
        }

        blockState.update(true)

        return true
    }
}

@Serializable
@SerialName("item")
/* Placed using invisible item frames */
class ItemBlockTemplate(
    val collision: Boolean,
    @SerialName("sit-height")
    override val sitHeight: Double? = null,
    override val drops: ItemStack? = null,
    @SerialName("can-fortune")
    override val canFortune: Boolean = false
) : BlockTemplate() {
    override fun place(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        val item = ctx.item ?: return false
        val key = ctx.item.customItemKey ?: return false

        val block = ctx.block ?: return false
        val blockFace = ctx.blockFace ?: return false

        val centerLocation = block.getRelative(blockFace).location.add(0.5, 0.5, 0.5)
        val nearbyItems = centerLocation.world.getNearbyEntities(centerLocation, 0.5, 0.5, 0.5) {
            it.type == EntityType.ITEM_FRAME &&
                    it.persistentDataContainer.has(
                        NamespacedKey(Moromoro.plugin.config.namespace, "type"),
                        PersistentDataType.STRING
                    )
        }
        if (nearbyItems.isNotEmpty()) {
            return false
        }

        val playerLocation = player.location.clone().apply { yaw += 180 }
        val rotation = playerLocation.rotation

        val location = block.getRelative(blockFace).location
        val world = location.world ?: return false

        // Try to place item frame
        val itemFrame = try {
            world.spawnEntity(location, EntityType.ITEM_FRAME) as ItemFrame
        } catch (e: IllegalArgumentException) {
            return false
        }.apply {
            setFacingDirection(blockFace, true)
            setRotation(rotation)
            isFixed = true
            isVisible = false

            // Set persistent data
            persistentDataContainer.set(
                NamespacedKey(Moromoro.plugin.config.namespace, "type"),
                PersistentDataType.STRING,
                key
            )
        }

        // Move item into item frame
        val cloned = item.clone()
        cloned.itemMeta = cloned.itemMeta.apply {
            setDisplayName(null)
        }
        cloned.amount = 1
        itemFrame.setItem(cloned, false)

        if (collision) {
            location.block.type = Material.BARRIER
        }

        return true
    }
}

class BlockManager(val plugin: Moromoro) {
    var blockTemplates: MutableMap<String, BlockTemplate> = mutableMapOf()
        private set
    var brownMushroomStates: MutableMap<Int, String> = mutableMapOf()
        private set
    var redMushroomStates: MutableMap<Int, String> = mutableMapOf()
        private set
    var mushroomStemStates: MutableMap<Int, String> = mutableMapOf()
        private set

    fun enable() {}

    fun disable() {}

    fun register(key: String, blockTemplate: BlockTemplate) {
        blockTemplates.put(key, blockTemplate)

        if (blockTemplate is MushroomBlockTemplate) {
            val material = blockTemplate.material
            val state = blockTemplate.state
            if (material.isRestrictedCustomBlockState(state)) {
                plugin.logger.warning("Restricted mushroom block state (${state}) used! This is used in vanilla Minecraft and will generate in the world.")
            }
            when (blockTemplate.material) {
                Material.BROWN_MUSHROOM_BLOCK -> brownMushroomStates
                Material.RED_MUSHROOM_BLOCK -> redMushroomStates
                Material.MUSHROOM_STEM -> mushroomStemStates
                else -> {
                    plugin.logger.warning("Expected 'material' matching one of: BROWN_MUSHROOM_BLOCK, RED_MUSHROOM_BLOCk, MUSHROOM_STEM. Found '${blockTemplate.material}' instead.")
                    null
                }
            }?.put(state, key)
        }
    }

    fun breakNaturally(tool: ItemStack, block: Block, dropItem: Boolean): Boolean {
        val states = when (block.type) {
            Material.BROWN_MUSHROOM_BLOCK -> brownMushroomStates
            Material.RED_MUSHROOM_BLOCK -> redMushroomStates
            Material.MUSHROOM_STEM -> mushroomStemStates
            else -> return block.breakNaturally(tool)
        }

        val state = block.customBlockState ?: return false

        val key = states[state] ?: return false

        val template = plugin.itemManager.templates[key] ?: return false

        block.setType(Material.AIR, true)

        if (dropItem) {
            val item = if (tool.enchantments.get(Enchantment.SILK_TOUCH) != null) {
                template.item(key, 1)
            } else {
                template.block?.drops?.clone() ?: template.item(key, 1)
            }

            if (template.block?.canFortune == true) {
                val fortune = tool.enchantments.get(Enchantment.LOOT_BONUS_BLOCKS) ?: 0
                val multiplier = 1 + max(0, Random.nextInt(fortune + 2) - 2)
                item.amount *= multiplier
            }

            block.world.dropItemNaturally(block.location, item)
        }

        block.playSoundAt(Sound.BLOCK_WOOD_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f)

        return true
    }
}

var BlockState.customBlockState: Int?
    get() {
        val multipleFacing = blockData as? MultipleFacing ?: return null
        var state = 0
        for (face in multipleFacing.faces) {
            when (face) {
                BlockFace.DOWN -> state += 1
                BlockFace.EAST -> state += 2
                BlockFace.NORTH -> state += 4
                BlockFace.SOUTH -> state += 8
                BlockFace.UP -> state += 16
                BlockFace.WEST -> state += 32
            }
        }
        return state
    }
    set(state: Int?) {
        val multipleFacing = blockData as? MultipleFacing ?: return
        var state = state ?: return
        if (state >= 32) {
            multipleFacing.setFace(BlockFace.WEST, true)
            state -= 32
        } else {
            multipleFacing.setFace(BlockFace.WEST, false)
        }

        if (state >= 16) {
            multipleFacing.setFace(BlockFace.UP, true)
            state -= 16
        } else {
            multipleFacing.setFace(BlockFace.UP, false)
        }

        if (state >= 8) {
            multipleFacing.setFace(BlockFace.SOUTH, true)
            state -= 8
        } else {
            multipleFacing.setFace(BlockFace.SOUTH, false)
        }

        if (state >= 4) {
            multipleFacing.setFace(BlockFace.NORTH, true)
            state -= 4
        } else {
            multipleFacing.setFace(BlockFace.NORTH, false)
        }

        if (state >= 2) {
            multipleFacing.setFace(BlockFace.EAST, true)
            state -= 2
        } else {
            multipleFacing.setFace(BlockFace.EAST, false)
        }

        if (state >= 1) {
            multipleFacing.setFace(BlockFace.DOWN, true)
            state -= 1
        } else {
            multipleFacing.setFace(BlockFace.DOWN, false)
        }

        setBlockData(multipleFacing)
    }

var Block.customBlockState: Int?
    get() {
        if (type != Material.BROWN_MUSHROOM_BLOCK
            && type != Material.RED_MUSHROOM_BLOCK
            && type != Material.MUSHROOM_STEM) {
            return null
        }

        val multipleFacing = blockData as? MultipleFacing ?: return null
        var state = 0
        for (face in multipleFacing.faces) {
            when (face) {
                BlockFace.DOWN -> state += 1
                BlockFace.EAST -> state += 2
                BlockFace.NORTH -> state += 4
                BlockFace.SOUTH -> state += 8
                BlockFace.UP -> state += 16
                BlockFace.WEST -> state += 32
            }
        }

        if (type.isRestrictedCustomBlockState(state)) {
            return null
        }

        return state
    }
    set(state: Int?) {
        if (type != Material.BROWN_MUSHROOM_BLOCK
            && type != Material.RED_MUSHROOM_BLOCK
            && type != Material.MUSHROOM_STEM) {
            return
        }
        val multipleFacing = blockData as? MultipleFacing ?: return
        var state = state ?: return
        if (state >= 32) {
            multipleFacing.setFace(BlockFace.WEST, true)
            state -= 32
        } else {
            multipleFacing.setFace(BlockFace.WEST, false)
        }

        if (state >= 16) {
            multipleFacing.setFace(BlockFace.UP, true)
            state -= 16
        } else {
            multipleFacing.setFace(BlockFace.UP, false)
        }

        if (state >= 8) {
            multipleFacing.setFace(BlockFace.SOUTH, true)
            state -= 8
        } else {
            multipleFacing.setFace(BlockFace.SOUTH, false)
        }

        if (state >= 4) {
            multipleFacing.setFace(BlockFace.NORTH, true)
            state -= 4
        } else {
            multipleFacing.setFace(BlockFace.NORTH, false)
        }

        if (state >= 2) {
            multipleFacing.setFace(BlockFace.EAST, true)
            state -= 2
        } else {
            multipleFacing.setFace(BlockFace.EAST, false)
        }

        if (state >= 1) {
            multipleFacing.setFace(BlockFace.DOWN, true)
            state -= 1
        } else {
            multipleFacing.setFace(BlockFace.DOWN, false)
        }

        setBlockData(multipleFacing, false)
    }

fun Material.isRestrictedCustomBlockState(state: Int): Boolean {
    return when (this) {
        Material.BROWN_MUSHROOM_BLOCK -> setOf(
            0b000000,
            0b010000,
            0b010010,
            0b010100,
            0b010110,
            0b011000,
            0b011010,
            0b110000,
            0b110100,
            0b111000,
            0b111111,
        )
        Material.RED_MUSHROOM_BLOCK -> setOf(
            0b000000,
            0b000010,
            0b000100,
            0b000110,
            0b001000,
            0b001010,
            0b010000,
            0b010010,
            0b010100,
            0b010110,
            0b011000,
            0b011010,
            0b100000,
            0b100100,
            0b101000,
            0b110000,
            0b110100,
            0b111000,
            0b111111,
        )
        Material.MUSHROOM_STEM -> setOf(
            0b000000,
            0b101110,
            0b111111,
        )
        else -> null
    }?.contains(state) ?: false
}