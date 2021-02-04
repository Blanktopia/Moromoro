package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Flow Control

@Serializable
@SerialName("if")
data class If(val condition: Action, val ifTrue: List<Action>, val ifFalse: List<Action>) : Action {
    override fun perform(ctx: Context): Boolean {
        return if (condition.perform(ctx)) {
            ifTrue.forEach { it.perform(ctx) }
            true
        } else {
            ifFalse.forEach { it.perform(ctx) }
            false
        }
    }
}

@Serializable
@SerialName("noop")
object Noop : Action {
    override fun perform(ctx: Context): Boolean {
        return false
    }
}
