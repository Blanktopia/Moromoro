package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("set-saturation")
data class SetSaturation(val saturation: Float) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        player.saturation = saturation
        return true
    }
}
