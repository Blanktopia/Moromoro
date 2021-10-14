package me.weiwen.moromoro.actions.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context

@Serializable
@SerialName("cancel")
object Cancel : Action {
    override fun perform(ctx: Context): Boolean {
        ctx.isCancelled = true
        return true
    }
}

