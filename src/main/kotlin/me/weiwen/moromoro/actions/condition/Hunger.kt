package me.weiwen.moromoro.actions.condition

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context

@Serializable
enum class Comparison {
    @SerialName("<")
    LESS_THAN,
    @SerialName(">")
    GREATER_THAN,
    @SerialName("=")
    EQUALS,
    @SerialName("!=")
    NOT_EQUALS,
}

@Serializable
@SerialName("hunger")
data class Hunger(val hunger: Int, val comparison: Comparison = Comparison.LESS_THAN) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        return when (comparison) {
            Comparison.LESS_THAN -> player.foodLevel < hunger
            Comparison.GREATER_THAN -> player.foodLevel > hunger
            Comparison.EQUALS -> player.foodLevel == hunger
            Comparison.NOT_EQUALS -> player.foodLevel != hunger
        }
    }
}
