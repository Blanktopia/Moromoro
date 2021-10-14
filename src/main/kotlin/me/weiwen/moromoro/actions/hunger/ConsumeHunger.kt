package me.weiwen.moromoro.actions.hunger

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context

@Serializable
@SerialName("consume-hunger")
data class ConsumeHunger(val amount: Float) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false

        if (player.saturation + player.foodLevel < amount) {
            return false
        }

        player.exhaustion += amount * 4

        return true
    }
}
