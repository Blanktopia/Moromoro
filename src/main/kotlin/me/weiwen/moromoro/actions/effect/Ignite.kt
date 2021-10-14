package me.weiwen.moromoro.actions.effect

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context

@Serializable
@SerialName("ignite")
data class Ignite(val duration: Int = 80) : Action {
    override fun perform(ctx: Context): Boolean {
        val entity = ctx.entity ?: return false
        entity.fireTicks = duration

        return true
    }
}

