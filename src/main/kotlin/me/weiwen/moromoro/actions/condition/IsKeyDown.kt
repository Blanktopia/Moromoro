package me.weiwen.moromoro.actions.condition

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import org.bukkit.Input

@Serializable
enum class InputKey {
    @SerialName("forward")
    FORWARD,
    @SerialName("backward")
    BACKWARD,
    @SerialName("left")
    LEFT,
    @SerialName("right")
    RIGHT,
    @SerialName("jump")
    JUMP,
    @SerialName("sneak")
    SNEAK,
    @SerialName("sprint")
    SPRINT
}

fun InputKey.isPressed(input: Input): Boolean {
    return when (this) {
        InputKey.FORWARD -> input.isForward
        InputKey.BACKWARD -> input.isBackward
        InputKey.LEFT -> input.isLeft
        InputKey.RIGHT -> input.isRight
        InputKey.JUMP -> input.isJump
        InputKey.SNEAK -> input.isSneak
        InputKey.SPRINT -> input.isSprint
    }
}

@Serializable
@SerialName("is-key-down")
data class IsKeyDown(val key: InputKey) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        return key.isPressed(player.currentInput)
    }
}

