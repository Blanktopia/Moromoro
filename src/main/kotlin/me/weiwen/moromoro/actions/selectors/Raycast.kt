package me.weiwen.moromoro.actions.selectors

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import org.bukkit.FluidCollisionMode

@Serializable
@SerialName("raycast")
data class Raycast(val range: Double, val actions: List<Action> = listOf()) : Action {
    override fun perform(ctx: Context): Boolean {
        val result = ctx.player?.rayTraceBlocks(range, FluidCollisionMode.NEVER) ?: return false

        ctx.entity = result.hitEntity
        ctx.block = result.hitBlock
        ctx.blockFace = result.hitBlockFace

        return actions.all { action ->
            action.perform(ctx)
        }
    }
}

