@file:UseSerializers(MaterialSerializer::class)

package me.weiwen.moromoro.actions.block

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.extensions.isPartiallyEmpty
import me.weiwen.moromoro.serializers.MaterialSerializer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Directional
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.EquipmentSlot

@Serializable
@SerialName("place-block")
data class PlaceBlock(val material: Material) : Action {
    override fun perform(ctx: Context): Boolean {
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

        return when (material) {
            Material.TORCH -> placeTorch(ctx, placedAgainst)
            else -> placeBlock(ctx, placedAgainst)
        }
    }

    private fun placeBlock(ctx: Context, placedAgainst: Block): Boolean {
        val player = ctx.player ?: return false
        val item = ctx.item ?: return false
        val block = ctx.block ?: return false

        val state = block.state
        state.type = material

        val buildEvent = BlockPlaceEvent(
            block,
            state,
            placedAgainst,
            item,
            player,
            true,
            EquipmentSlot.HAND
        )
        Bukkit.getPluginManager().callEvent(buildEvent)
        if (buildEvent.isCancelled) return false

        state.update(true)

        return true
    }

    private fun placeTorch(ctx: Context, placedAgainst: Block): Boolean {
        val player = ctx.player ?: return false
        val item = ctx.item ?: return false
        val block = ctx.block ?: return false
        val face = ctx.blockFace ?: return false

        val state = block.state
        if (placedAgainst.type.isSolid && face != BlockFace.DOWN) {
            when (face) {
                BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH -> {
                    state.type = Material.WALL_TORCH
                    val data = Bukkit.getServer().createBlockData(Material.WALL_TORCH)
                    (data as? Directional)?.facing = face
                    state.blockData = data
                }
                else -> {
                    state.type = Material.TORCH
                    val data = Bukkit.getServer().createBlockData(Material.TORCH)
                    state.blockData = data
                }
            }
        } else {
            var canPlace = false
            for (tryFace in listOf(BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH)) {
                if (block.getRelative(tryFace).type.isSolid) {
                    state.type = Material.WALL_TORCH
                    val data = Bukkit.getServer().createBlockData(Material.WALL_TORCH)
                    (data as? Directional)?.facing = tryFace.oppositeFace
                    state.blockData = data
                    canPlace = true
                    break
                }
            }
            if (!canPlace && block.getRelative(BlockFace.DOWN).type.isSolid) {
                state.type = Material.TORCH
                val data = Bukkit.getServer().createBlockData(Material.TORCH)
                state.blockData = data
                canPlace = true
            }
            if (!canPlace) return false
        }

        val buildEvent = BlockPlaceEvent(
            block,
            state,
            placedAgainst,
            item,
            player,
            true,
            EquipmentSlot.HAND
        )
        Bukkit.getPluginManager().callEvent(buildEvent)
        if (buildEvent.isCancelled) return false

        state.update(true)

        return true
    }
}
