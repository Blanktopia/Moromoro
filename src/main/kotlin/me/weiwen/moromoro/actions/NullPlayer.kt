package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("null-player")
object NullPlayer : Action {
    override fun perform(ctx: Context): Boolean {
        ctx.player = null

        return true
    }
}
