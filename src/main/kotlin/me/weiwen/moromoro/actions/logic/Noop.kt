package me.weiwen.moromoro.actions.logic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context

@Serializable
@SerialName("noop")
object Noop : Action {
    override fun perform(ctx: Context): Boolean {
        return true
    }
}

