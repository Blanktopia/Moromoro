package me.weiwen.moromoro.actions.selectors

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context

@Serializable
@SerialName("use-player-facing")
object UsePlayerFacing : Action {
    override fun perform(ctx: Context): Boolean {
        ctx.blockFace = ctx.player?.facing
        return true
    }
}

