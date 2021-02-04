package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("and")
data class And(val actions: List<Action>) : Action {
    override fun perform(ctx: Context): Boolean {
        return actions.all { it.perform(ctx) }
    }
}

@Serializable
@SerialName("or")
data class Or(val actions: List<Action>) : Action {
    override fun perform(ctx: Context): Boolean {
        return actions.any { it.perform(ctx) }
    }
}

