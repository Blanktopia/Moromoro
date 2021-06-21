package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("consume-hunger")
data class ConsumeHunger(val amount: Float) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false

        player.exhaustion += amount * 4

        return true
    }
}
