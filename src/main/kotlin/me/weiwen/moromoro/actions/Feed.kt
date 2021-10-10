package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
