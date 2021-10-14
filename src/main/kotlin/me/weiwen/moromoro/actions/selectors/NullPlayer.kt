package me.weiwen.moromoro.actions.selectors

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context

@Serializable
@SerialName("null-player")
object NullPlayer : Action {
    override fun perform(ctx: Context): Boolean {
        ctx.player = null

        return true
    }
}
