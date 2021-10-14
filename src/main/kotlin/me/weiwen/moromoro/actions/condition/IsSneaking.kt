package me.weiwen.moromoro.actions.condition

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context

@Serializable
@SerialName("is-sneaking")
object IsSneaking : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        return player.isSneaking
    }
}

