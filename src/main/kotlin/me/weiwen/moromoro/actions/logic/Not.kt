package me.weiwen.moromoro.actions.logic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context

@Serializable
@SerialName("not")
data class Not(val not: Action) : Action {
    override fun perform(ctx: Context): Boolean {
        return !not.perform(ctx)
    }
}

