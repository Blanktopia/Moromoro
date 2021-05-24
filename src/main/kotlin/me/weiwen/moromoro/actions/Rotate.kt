package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.extensions.canBuildAt
import me.weiwen.moromoro.extensions.isPartial
import me.weiwen.moromoro.extensions.playSoundAt
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.*
import org.bukkit.block.data.type.Slab

val SIGN_ROTATIONS = listOf(
    BlockFace.NORTH, BlockFace.NORTH_NORTH_EAST, BlockFace.NORTH_EAST, BlockFace.EAST_NORTH_EAST,
    BlockFace.EAST, BlockFace.EAST_SOUTH_EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_SOUTH_EAST,
    BlockFace.SOUTH, BlockFace.SOUTH_SOUTH_WEST, BlockFace.SOUTH_WEST, BlockFace.WEST_SOUTH_WEST,
    BlockFace.WEST, BlockFace.WEST_NORTH_WEST, BlockFace.NORTH_WEST, BlockFace.NORTH_NORTH_WEST)

@Serializable
@SerialName("rotate")
class Rotate(val reversed: Boolean = false) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player
        val block = ctx.block ?: return false
        val face = ctx.blockFace ?: return false

        if (!player.canBuildAt(block.location)) {
            return false
        }

        if (block.type.isPartial) {
            return false
        }

        val rotated = if (reversed) backward(block, face) else forward(block, face)

        if (rotated) {
            block.playSoundAt(Sound.BLOCK_GRINDSTONE_USE, SoundCategory.PLAYERS, 1.0f, 2.0f)
        }

        return true
    }

    private fun forward(block: Block, face: BlockFace): Boolean {
        val data = block.blockData
        when (data) {
            is Directional -> {
                val faces = data.faces.sorted()
                val index = faces.indexOf(data.facing) + 1
                data.facing = faces[index % faces.size]
            }
            is MultipleFacing -> {
                if (data.allowedFaces.contains(face)) {
                    data.setFace(face, !data.hasFace(face))
                }
            }
            is Orientable -> {
                val axes = data.axes.sorted()
                val index = axes.indexOf(data.axis) + 1
                data.axis = axes[index % axes.size]
            }
            is Rotatable -> {
                val index = SIGN_ROTATIONS.indexOf(data.rotation) + 1
                data.rotation = SIGN_ROTATIONS[index % SIGN_ROTATIONS.size]
            }
            is Rail -> {
                val shapes = data.shapes.sorted()
                val index = shapes.indexOf(data.shape) + 1
                data.shape = shapes[index % shapes.size]
            }
            is Bisected -> {
                data.half = if (data.half == Bisected.Half.BOTTOM) Bisected.Half.TOP else Bisected.Half.BOTTOM
            }
            is Slab -> {
                if (data.type == Slab.Type.DOUBLE) return false
                data.type = if (data.type == Slab.Type.BOTTOM) Slab.Type.TOP else Slab.Type.BOTTOM
            }
            else -> return false
        }

        block.setBlockData(data, false)

        return true
    }

    private fun backward(block: Block, face: BlockFace): Boolean {
        val data = block.blockData
        when (data) {
            is Slab -> {
                if (data.type == Slab.Type.DOUBLE) return false
                data.type = if (data.type == Slab.Type.BOTTOM) Slab.Type.TOP else Slab.Type.BOTTOM
            }
            is Bisected -> {
                data.half = if (data.half == Bisected.Half.BOTTOM) Bisected.Half.TOP else Bisected.Half.BOTTOM
            }
            is Rail -> {
                val shapes = data.shapes.sorted()
                val index = shapes.size + shapes.indexOf(data.shape) - 1
                data.shape = shapes[index % shapes.size]
            }
            is Rotatable -> {
                val index = SIGN_ROTATIONS.size + SIGN_ROTATIONS.indexOf(data.rotation) - 1
                data.rotation = SIGN_ROTATIONS[index % SIGN_ROTATIONS.size]
            }
            is Orientable -> {
                val axes = data.axes.sorted()
                val index = axes.size + axes.indexOf(data.axis) - 1
                data.axis = axes[index % axes.size]
            }
            is MultipleFacing -> {
                if (data.allowedFaces.contains(face)) {
                    data.setFace(face, !data.hasFace(face))
                }
            }
            is Directional -> {
                val faces = data.faces.sorted()
                val index = faces.size + faces.indexOf(data.facing) + 1
                data.facing = faces[index % faces.size]
            }
            else -> return false
        }

        block.setBlockData(data, false)

        return true
    }
}

