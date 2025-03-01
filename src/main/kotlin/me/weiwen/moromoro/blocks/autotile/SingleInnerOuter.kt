package me.weiwen.moromoro.blocks.autotile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.blocks.CustomBlock

@Serializable
@SerialName("single-inner-outer")
data class SingleInnerOuter(val keys: List<String>) : AutoTile {
    override fun autotile(sides: Sides): Pair<String, Float>? {
        var bitset = 0
        bitset += if (sides.isLeft) 0b1000 else 0
        bitset += if (sides.isForward) 0b0100 else 0
        bitset += if (sides.isRight) 0b0010 else 0
        bitset += if (sides.isBackward) 0b0001 else 0

        return when (bitset) {
            0b0110 -> keys.getOrNull(2)?.let { Pair(it, 0f) }
            0b0011 -> keys.getOrNull(1)?.let { Pair(it, 270f) }
            0b1001 -> keys.getOrNull(1)?.let { Pair(it, 0f) }
            0b1100 -> keys.getOrNull(2)?.let { Pair(it, 270f) }
            else -> keys.getOrNull(0)?.let { Pair(it, 0f) }
        }
    }

    override fun neighbours(sides: Sides): Sequence<CustomBlock> {
        return sequenceOf(sides.left, sides.right, sides.forward, sides.backward).filterNotNull()
    }
}