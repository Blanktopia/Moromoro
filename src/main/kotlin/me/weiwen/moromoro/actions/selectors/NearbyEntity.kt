package me.weiwen.moromoro.actions.selectors

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context

@Serializable
@SerialName("nearby-living-entity")
data class NearbyLivingEntity(val range: Double, val actions: List<Action> = listOf()) : Action {
    override fun perform(ctx: Context): Boolean {
        val location = ctx.projectile?.location
            ?: ctx.entity?.location
            ?: ctx.block?.location
            ?: ctx.player?.location
            ?: return false

        // isCancelled is not propagated by design
        val ctxs = location.getNearbyLivingEntities(range, range, range).map {
            Context(
                event = ctx.event,
                player = ctx.player,
                item = ctx.item,
                entity = it,
                block = ctx.block,
                blockFace = ctx.blockFace,
                projectile = ctx.projectile
            )
        }

        return actions.all { action ->
            ctxs.all { ctx ->
                action.perform(ctx)
            }
        }
    }
}

