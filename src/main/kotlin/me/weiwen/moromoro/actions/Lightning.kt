package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("lightning")
object Lightning : Action {
    override fun perform(ctx: Context): Boolean {
        val location = ctx.entity?.location ?: ctx.block?.location ?: return false

        location.world.strikeLightning(location)

        return true
    }
}

