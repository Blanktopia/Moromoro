package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("set-starvation-rate")
data class SetStarvationRate(val rate: Int = 80) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        player.starvationRate = rate
        return true
    }
}
