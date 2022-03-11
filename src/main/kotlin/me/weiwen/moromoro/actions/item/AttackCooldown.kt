package me.weiwen.moromoro.actions.item

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context

@Serializable
@SerialName("attack-cooldown")
object AttackCooldown : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false

        if (player.attackCooldown < 1.0) {
            return false
        }

        return true
    }
}

