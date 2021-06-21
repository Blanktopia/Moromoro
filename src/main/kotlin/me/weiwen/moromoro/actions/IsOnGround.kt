package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("is-on-ground")
object IsOnGround : Action {
    override fun perform(ctx: Context): Boolean {
        // TODO: don't use Player#isOnGround
        val player = ctx.player ?: return false
        return player.isOnGround
    }
}

