@file:UseSerializers(
    ItemStackSerializer::class,
)

package me.weiwen.moromoro.blocks

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.BlockTrigger
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.extensions.playSoundAt
import me.weiwen.moromoro.managers.customBlockState
import me.weiwen.moromoro.serializers.ItemStackSerializer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.SoundCategory
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

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

    override val triggers: Map<BlockTrigger, List<Action>> = mapOf(),
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
