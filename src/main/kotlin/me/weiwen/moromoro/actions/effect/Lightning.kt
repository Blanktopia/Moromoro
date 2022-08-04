package me.weiwen.moromoro.actions.effect

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.actions.LocationSelector

@Serializable
@SerialName("lightning")
data class Lightning(
    val location: LocationSelector,
) : Action {
    override fun perform(ctx: Context): Boolean {
        val location = location.location(ctx) ?: return false

        location.world.strikeLightning(location)

        return true
    }
}

