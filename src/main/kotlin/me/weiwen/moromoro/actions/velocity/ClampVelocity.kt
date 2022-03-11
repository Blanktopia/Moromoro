package me.weiwen.moromoro.actions.velocity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context

@Serializable
@SerialName("clamp-velocity")
data class ClampVelocity(val max: Double) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false

        if (player.velocity.lengthSquared() > max * max) {
            player.velocity = player.velocity.normalize().multiply(max)
        }

        return true
    }
}

