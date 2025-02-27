package me.weiwen.moromoro.actions.condition

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import org.bukkit.entity.Entity

@Serializable
@SerialName("is-on-ground")
object IsOnGround : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        return (player as Entity).isOnGround
    }
}

