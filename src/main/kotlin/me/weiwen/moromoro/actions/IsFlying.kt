package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("is-flying")
object IsFlying : Action {
    override fun perform(ctx: Context): Boolean {
        return ctx.player.isFlying
    }
}

