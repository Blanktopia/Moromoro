package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("set-hunger")
data class SetHunger(val hunger: Int) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        player.foodLevel = hunger
        return true
    }
}
