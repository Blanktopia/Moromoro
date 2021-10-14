package me.weiwen.moromoro.actions.velocity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context

@Serializable
@SerialName("multiply-velocity")
data class MultiplyVelocity(val multiplier: Double) : Action {
    override fun perform(ctx: Context): Boolean {
        val entity = ctx.projectile ?: ctx.entity ?: ctx.player ?: return false

        entity.velocity = entity.velocity.multiply(multiplier)

        return true
    }
}

