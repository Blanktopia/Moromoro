package me.weiwen.moromoro.blocks.autotile

import kotlinx.serialization.Serializable
import me.weiwen.moromoro.blocks.CustomBlock
import org.bukkit.block.Block
import org.bukkit.block.BlockFace

@Serializable
sealed interface AutoTile {
    fun autotile(sides: Sides): Pair<String, Float>?
    fun neighbours(sides: Sides): Sequence<CustomBlock>
}

data class Sides(val key: String, val block: Block, val facing: BlockFace) {
    val forward: CustomBlock? by lazy { CustomBlock.fromBlock(block.getRelative(facing)) }
    val backward: CustomBlock? by lazy { CustomBlock.fromBlock(block.getRelative(facing.oppositeFace)) }
    val left: CustomBlock? by lazy { CustomBlock.fromBlock(block.getRelative(facing.left)) }
    val right: CustomBlock? by lazy { CustomBlock.fromBlock(block.getRelative(facing.right)) }

    val isForward = forward?.key == key
    val isBackward = backward?.key == key
    val isLeft = left?.key == key
    val isRight = right?.key == key
}

val BlockFace.left: BlockFace
    get() = when (this) {
        BlockFace.NORTH -> BlockFace.WEST
        BlockFace.SOUTH -> BlockFace.EAST
        BlockFace.WEST -> BlockFace.SOUTH
        BlockFace.EAST -> BlockFace.NORTH
        else -> this
    }
val BlockFace.right: BlockFace
    get() = left.oppositeFace