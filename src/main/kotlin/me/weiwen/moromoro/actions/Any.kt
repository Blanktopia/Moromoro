package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("any")
data class Any(val actions: List<Action>) : Action {
    override fun perform(ctx: Context): Boolean {
        return actions.any { it.perform(ctx) }
    }
}

