package me.weiwen.moromoro.actions.hunger

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context

@Serializable
@SerialName("set-unsaturated-regen-rate")
data class SetUnsaturatedRegenRate(val rate: Int = 80) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        player.unsaturatedRegenRate = rate
        return true
    }
}
