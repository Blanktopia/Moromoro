@file:UseSerializers(
    ItemStackSerializer::class,
    MaterialSerializer::class,
    EnchantmentSerializer::class,
    ColorSerializer::class,
    UUIDSerializer::class
)

package me.weiwen.moromoro.blocks

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.extensions.customItemKey
import me.weiwen.moromoro.extensions.playSoundAt
import me.weiwen.moromoro.extensions.rotation
import me.weiwen.moromoro.managers.customBlockState
import me.weiwen.moromoro.serializers.*
import org.bukkit.*
import org.bukkit.block.BlockFace
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemFrame
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

@Serializable
sealed class BlockTemplate {
    abstract fun place(ctx: Context): Boolean

    @SerialName("sit-height")
    abstract val sitHeight: Double?

    abstract val drops: List<ItemStack>?
    abstract val experience: Int

    @SerialName("can-fortune")
    abstract val canFortune: Boolean

    abstract val hardness: Double
    abstract val tools: List<ItemStack>?

    abstract val sounds: SoundGroup
}

@Serializable
data class SoundGroup(
    val `break`: Sound? = null,
    val fall: Sound? = null,
    val hit: Sound? = null,
    val place: Sound? = null,
    val step: Sound? = null,
)

@Serializable
data class Sound(
    val sound: String,
    val volume: Float,
    val pitch: Float,
)

@Serializable
@SerialName("mushroom")
class MushroomBlockTemplate(
    val state: Int,
    val material: Material = Material.BROWN_MUSHROOM_BLOCK,
    val model: String,

    @SerialName("sit-height")
    override val sitHeight: Double? = null,

    override val drops: List<ItemStack>? = null,
    override val experience: Int = 0,
    @SerialName("can-fortune")
    override val canFortune: Boolean = false,

    override val hardness: Double = 1.0,
    override val tools: List<ItemStack>? = null,

    override val sounds: SoundGroup = SoundGroup(),
) : BlockTemplate() {
    override fun place(ctx: Context): Boolean {
        val player = ctx.player
        val item = ctx.item ?: return false

        val placedAgainst = ctx.block ?: return false
        val blockFace = ctx.blockFace ?: return false
        val placedBlock = placedAgainst.getRelative(blockFace)

        val blockState = placedBlock.state.apply {
            type = Material.BROWN_MUSHROOM_BLOCK
            customBlockState = state
        }

        if (player != null) {
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
        }

        blockState.update(true)

        sounds.place.let {
            placedBlock.playSoundAt(
                it?.sound ?: "block.wood.place",
                SoundCategory.BLOCKS,
                it?.volume ?: 0.25f,
                it?.pitch ?: 1f
            )
        }

        return true
    }
}

@Serializable
@SerialName("item")
/* Placed using invisible item frames */
class ItemBlockTemplate(
    private val collision: Boolean = false,

    @SerialName("sit-height")
    override val sitHeight: Double? = null,

    override val drops: List<ItemStack>? = null,
    override val experience: Int = 0,
    @SerialName("can-fortune")
    override val canFortune: Boolean = false,

    override val hardness: Double = 1.0,
    override val tools: List<ItemStack>? = null,

    override val sounds: SoundGroup = SoundGroup(),
) : BlockTemplate() {
    override fun place(ctx: Context): Boolean {
        val player = ctx.player
        val item = ctx.item ?: return false
        val key = ctx.item.customItemKey ?: return false

        val block = ctx.block ?: return false
        val blockFace = ctx.blockFace ?: BlockFace.UP

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

        val rotation = player?.location?.clone()?.apply { yaw += 180 }?.rotation ?: Rotation.NONE

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

        sounds.place.let {
            location.block.playSoundAt(
                it?.sound ?: "block.wood.place",
                SoundCategory.BLOCKS,
                it?.volume ?: 1f,
                it?.pitch ?: 1f
            )
        }

        return true
    }
}
