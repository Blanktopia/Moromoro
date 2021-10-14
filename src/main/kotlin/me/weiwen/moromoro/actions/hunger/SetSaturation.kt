package me.weiwen.moromoro.actions.hunger

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context

@Serializable
@SerialName("set-saturation")
data class SetSaturation(val saturation: Float) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        player.saturation = saturation
        return true
    }
}
