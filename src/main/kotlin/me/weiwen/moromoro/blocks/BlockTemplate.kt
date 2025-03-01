@file:UseSerializers(
    ItemStackSerializer::class,
    ItemTypeSerializer::class,
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
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.ItemFrame
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import kotlin.math.roundToInt

@Serializable
sealed class BlockTemplate {
    abstract fun place(ctx: Context): Boolean

    @SerialName("sit-height")
    abstract val sitHeight: Double?
    @SerialName("sit-rotate")
    abstract val sitRotate: Boolean?

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
    @SerialName("sit-rotate")
    override val sitRotate: Boolean? = null,

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
@SerialName("itemframe")
/* Placed using block display entities*/
class ItemBlockTemplate(
    private val collision: Boolean = false,

    @SerialName("sit-height")
    override val sitHeight: Double? = null,
    @SerialName("sit-rotate")
    override val sitRotate: Boolean? = null,

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
            it.type == EntityType.ITEM_FRAME || it.type == EntityType.ITEM_DISPLAY &&
                    it.persistentDataContainer.has(
                        NamespacedKey(Moromoro.plugin.config.namespace, "type"),
                        PersistentDataType.STRING
                    )
        }
        if (nearbyItems.isNotEmpty()) {
            return false
        }

        val rotation = if (blockFace == BlockFace.UP || blockFace == BlockFace.DOWN) {
            player?.location?.clone()?.apply { yaw += 180 }?.rotation ?: Rotation.NONE
        } else {
            Rotation.NONE
        }

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

@Serializable
@SerialName("item")
/* Placed using item display entities*/
class ItemDisplayBlockTemplate(
    private val collision: Boolean = false,

    @SerialName("sit-height")
    override val sitHeight: Double? = null,
    @SerialName("sit-rotate")
    override val sitRotate: Boolean? = null,

    override val drops: List<ItemStack>? = null,
    override val experience: Int = 0,
    @SerialName("can-fortune")
    override val canFortune: Boolean = false,

    override val hardness: Double = 1.0,
    override val tools: List<ItemStack>? = null,

    override val sounds: SoundGroup = SoundGroup(),

    val pitch: Float = 0f,
    val yaw: Float = 0f,

) : BlockTemplate() {
    override fun place(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        val item = ctx.item ?: return false
        val key = ctx.item.customItemKey ?: return false

        val block = ctx.block ?: return false
        val blockFace = ctx.blockFace ?: BlockFace.UP

        val centerLocation = block.getRelative(blockFace).location.add(0.5, 0.5, 0.5)
        val nearbyItems = centerLocation.world.getNearbyEntities(centerLocation, 0.5, 0.5, 0.5) {
            it.type == EntityType.ITEM_FRAME || it.type == EntityType.ITEM_DISPLAY &&
                    it.persistentDataContainer.has(
                        NamespacedKey(Moromoro.plugin.config.namespace, "type"),
                        PersistentDataType.STRING
                    )
        }
        if (nearbyItems.isNotEmpty()) {
            return false
        }

        val location = block.getRelative(blockFace).location
        val world = location.world ?: return false

        val rotation = if (blockFace == BlockFace.UP || blockFace == BlockFace.DOWN) {
            player.location?.clone()?.apply { yaw += 180 }?.rotation ?: Rotation.NONE
        } else {
            Rotation.NONE
        }

        // Try to place item display
        val itemDisplay = try {
            world.spawnEntity(location.add(0.5, 0.5, 0.5).let {
                if (blockFace == BlockFace.UP || blockFace == BlockFace.DOWN) {
                    val preciseYaw = player.location.yaw.plus(180)
                    it.yaw = if (player.isSneaking) {
                        preciseYaw
                    } else {
                        45f * (preciseYaw / 45).roundToInt()
                    }
                    if (blockFace == BlockFace.DOWN) {
                        it.pitch = 180f
                    }
                } else {
                    it.direction = blockFace.direction
                    it.pitch = 90f
                }
                it.pitch += pitch
                it.yaw += yaw
                it
            }, EntityType.ITEM_DISPLAY) as ItemDisplay
        } catch (e: IllegalArgumentException) {
            return false
        }.apply {
            // Set persistent data
            persistentDataContainer.set(
                NamespacedKey(Moromoro.plugin.config.namespace, "type"),
                PersistentDataType.STRING,
                key
            )
        }

        // Move item into item frame
        itemDisplay.setItemStack(item.clone())

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
