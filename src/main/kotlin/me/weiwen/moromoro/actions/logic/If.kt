package me.weiwen.moromoro.actions.logic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context

@Serializable
@SerialName("if")
data class If(val `if`: Action, val then: List<Action> = listOf(), val `else`: List<Action> = listOf()) :
    Action {
    override fun perform(ctx: Context): Boolean {
        return if (`if`.perform(ctx)) {
            then.forEach { it.perform(ctx) }
            true
        } else {
            `else`.forEach { it.perform(ctx) }
            false
        }
    }
}

