package me.weiwen.moromoro.actions.item

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context

@Serializable
@SerialName("item-cooldown")
data class ItemCooldown(val ticks: Int = 0) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        val item = ctx.item ?: return false

        if (player.hasCooldown(item)) {
            return false
        }

        if (ticks > 0) {
            player.setCooldown(item, ticks)
        }

        return true
    }
}

