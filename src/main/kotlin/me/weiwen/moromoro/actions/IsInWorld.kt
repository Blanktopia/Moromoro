package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("is-in-world")
data class IsInWorld(val world: String) : Action {
    override fun perform(ctx: Context): Boolean {
        return ctx.player.world.name == world
    }
}

