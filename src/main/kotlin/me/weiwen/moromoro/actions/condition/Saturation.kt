package me.weiwen.moromoro.actions.condition

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context

@Serializable
@SerialName("saturation")
data class Saturation(val saturation: Float, val comparison: Comparison = Comparison.LESS_THAN) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        return when (comparison) {
            Comparison.LESS_THAN -> player.saturation < saturation
            Comparison.GREATER_THAN -> player.saturation > saturation
            Comparison.EQUALS -> player.saturation == saturation
            Comparison.NOT_EQUALS -> player.saturation != saturation
        }
    }
}
