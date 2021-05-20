package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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

        val (targetBlock, replaceBlock, targetFace) = when (block.type) {
            Material.GRASS, Material.TALL_GRASS, Material.FERN, Material.LARGE_FERN, Material.SNOW -> Triple(
                block.getRelative(
                    BlockFace.DOWN
                ), block, BlockFace.DOWN
            )
            else -> Triple(block, block.getRelative(face), face)
        }

        if (!replaceBlock.isEmpty) return false

        ctx.block = replaceBlock
        ctx.blockFace = targetFace

        return when (material) {
            Material.TORCH -> placeTorch(ctx, replaceBlock)
            else -> placeBlock(ctx, replaceBlock)
        }
    }

    private fun placeBlock(ctx: Context, replaceBlock: Block): Boolean {
        val block = ctx.block ?: return false
        val face = ctx.blockFace ?: return false

        val state = block.state
        state.type = material

        val buildEvent = BlockPlaceEvent(
            replaceBlock,
            state,
            block,
            ctx.item,
            ctx.player,
            true,
            EquipmentSlot.HAND
        )
        Bukkit.getPluginManager().callEvent(buildEvent)
        if (buildEvent.isCancelled) return false

        state.update(true)

        return true
    }

    private fun placeTorch(ctx: Context, replaceBlock: Block): Boolean {
        val block = ctx.block ?: return false
        val face = ctx.blockFace ?: return false

        val state = block.state
        if (block.type.isSolid && face != BlockFace.DOWN) {
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
                if (replaceBlock.getRelative(tryFace).type.isSolid) {
                    state.type = Material.WALL_TORCH
                    val data = Bukkit.getServer().createBlockData(Material.WALL_TORCH)
                    (data as? Directional)?.facing = tryFace.oppositeFace
                    state.blockData = data
                    canPlace = true
                    break
                }
            }
            if (!canPlace && replaceBlock.getRelative(BlockFace.DOWN).type.isSolid) {
                state.type = Material.TORCH
                val data = Bukkit.getServer().createBlockData(Material.TORCH)
                state.blockData = data
                canPlace = true
            }
            if (!canPlace) return false
        }

        val buildEvent = BlockPlaceEvent(
            replaceBlock,
            state,
            block,
            ctx.item,
            ctx.player,
            true,
            EquipmentSlot.HAND
        )
        Bukkit.getPluginManager().callEvent(buildEvent)
        if (buildEvent.isCancelled) return false

        state.update(true)

        return true
    }
}
