@file:UseSerializers(MaterialSerializer::class)

package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.extensions.isPartiallyEmpty
import me.weiwen.moromoro.extensions.playSoundAt
import me.weiwen.moromoro.serializers.MaterialSerializer
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.SoundCategory
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Directional
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

@Serializable
@SerialName("place-random-block")
object PlaceRandomBlock : Action {
    override fun perform(ctx: Context): Boolean {
        val player =ctx.player
        val block = ctx.block ?: return false
        val face = ctx.blockFace ?: return false

        val (placedAgainst, placedBlock, targetFace) = when (block.type) {
            Material.GRASS, Material.TALL_GRASS, Material.FERN, Material.LARGE_FERN, Material.SNOW -> Triple(
                block.getRelative(
                    BlockFace.DOWN
                ), block, BlockFace.DOWN
            )
            else -> Triple(block, block.getRelative(face), face)
        }

        if (!placedBlock.type.isPartiallyEmpty) return false

        ctx.block = placedBlock
        ctx.blockFace = targetFace

        for (slot in (0..8).toMutableList().shuffled()) {
            val item = player.inventory.getItem(slot) ?: continue

            if (!item.type.isBlock) {
                continue
            }

            if (placeBlock(ctx, item.type, placedAgainst)) {
                placedBlock.playSoundAt(placedBlock.soundGroup.placeSound, SoundCategory.BLOCKS, 1.0f, 1.0f)
                return true
            }
        }
        return false
    }

    private fun placeBlock(ctx: Context, material: Material, placedAgainst: Block): Boolean {
        val block = ctx.block ?: return false

        val state = block.state
        state.type = material

        val cost = ItemStack(material, 1)

        val buildEvent = BlockPlaceEvent(
            block,
            state,
            placedAgainst,
            cost,
            ctx.player,
            true,
            EquipmentSlot.HAND
        )
        Bukkit.getPluginManager().callEvent(buildEvent)
        if (buildEvent.isCancelled) return false

        val player = ctx.player
        if (player.gameMode != GameMode.CREATIVE) {
            val couldntRemove = player.inventory.removeItem(cost)
            if (couldntRemove.isNotEmpty()) {
                return false
            }
        }

        state.update(true)

        return true
    }
}
