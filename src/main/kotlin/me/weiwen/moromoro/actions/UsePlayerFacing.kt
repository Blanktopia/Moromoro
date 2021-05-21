package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("use-player-facing")
object UsePlayerFacing : Action {
    override fun perform(ctx: Context): Boolean {
        ctx.blockFace = ctx.player.facing
        return true
    }
}

