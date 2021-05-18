package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Flow Control

@Serializable
@SerialName("if")
data class If(val condition: Action, val ifTrue: List<Action> = listOf(), val ifFalse: List<Action> = listOf()) :
    Action {
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

@Serializable
@SerialName("all-players")
data class AllPlayers(val actions: List<Action> = listOf()) : Action {
    override fun perform(ctx: Context): Boolean {
        val ctxs = ctx.player.server.onlinePlayers.map {
            Context(
                event = ctx.event,
                player = it,
                item = ctx.item,
                entity = ctx.entity,
                block = ctx.block,
                blockFace = ctx.blockFace
            )
        }
        return actions.all { action ->
            ctxs.all { ctx ->
                action.perform(ctx)
            }
        }
    }
}

