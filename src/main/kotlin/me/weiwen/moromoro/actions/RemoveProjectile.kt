package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("remove-projectile")
object RemoveProjectile : Action {
    override fun perform(ctx: Context): Boolean {
        ctx.projectile?.remove()

        return true
    }
}

