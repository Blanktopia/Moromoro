@file:UseSerializers(
    ItemStackSerializer::class,
)

package me.weiwen.moromoro.blocks

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.BlockTrigger
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.extensions.customItemKey
import me.weiwen.moromoro.extensions.playSoundAt
import me.weiwen.moromoro.extensions.rotation
import me.weiwen.moromoro.serializers.ItemStackSerializer
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Rotation
import org.bukkit.SoundCategory
import org.bukkit.block.BlockFace
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemFrame
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

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

    override val triggers: Map<BlockTrigger, List<Action>> = mapOf(),
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
