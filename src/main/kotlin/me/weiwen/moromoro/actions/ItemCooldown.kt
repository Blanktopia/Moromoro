package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("item-cooldown")
data class ItemCooldown(val ticks: Int = 0) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player
        val material = ctx.item.type

        if (ctx.player.hasCooldown(ctx.item.type)) {
            return false
        }

        if (ticks > 0) {
            player.setCooldown(material, ticks)
        }

        return true
    }
}

