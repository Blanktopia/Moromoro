package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("all")
data class All(val actions: List<Action>) : Action {
    override fun perform(ctx: Context): Boolean {
        return actions.all { it.perform(ctx) }
    }
}

