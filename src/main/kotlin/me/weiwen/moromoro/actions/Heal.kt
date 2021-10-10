package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("heal")
data class Heal(val amount: Double) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        player.health += amount
        return true
    }
}
