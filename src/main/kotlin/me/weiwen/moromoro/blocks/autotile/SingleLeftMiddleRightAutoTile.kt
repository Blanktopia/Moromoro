package me.weiwen.moromoro.blocks.autotile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.blocks.CustomBlock

@Serializable
@SerialName("single-left-both-right")
data class SingleLeftBothRight(val keys: List<String>) : AutoTile {
    override fun autotile(sides: Sides): Pair<String, Float>? {
        return when {
            !sides.isLeft && sides.isRight -> keys.getOrNull(1)?.let { Pair(it, 0f) }
            sides.isLeft && sides.isRight -> keys.getOrNull(2)?.let { Pair(it, 0f) }
            sides.isLeft && !sides.isRight -> keys.getOrNull(3)?.let { Pair(it, 0f) }
            else -> keys.getOrNull(0)?.let { Pair(it, 0f) }
        }
    }

    override fun neighbours(sides: Sides): Sequence<CustomBlock> {
        return sequenceOf(sides.left, sides.right).filterNotNull()
    }
}