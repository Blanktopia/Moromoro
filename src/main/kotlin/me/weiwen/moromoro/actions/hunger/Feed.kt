package me.weiwen.moromoro.actions.hunger

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context

@Serializable
@SerialName("feed")
data class Feed(val amount: Int, val saturation: Float) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        player.foodLevel += amount
        player.saturation += saturation
        return true
    }
}
