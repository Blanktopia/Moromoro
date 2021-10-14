package me.weiwen.moromoro.actions.projectile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context

@Serializable
@SerialName("remove-projectile")
object RemoveProjectile : Action {
    override fun perform(ctx: Context): Boolean {
        ctx.projectile?.remove()

        return true
    }
}

