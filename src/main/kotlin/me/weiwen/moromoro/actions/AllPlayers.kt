package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("all-players")
data class AllPlayers(val actions: List<Action> = listOf()) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false

        // isCancelled is not propagated by design
        val ctxs = player.server.onlinePlayers.map {
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

