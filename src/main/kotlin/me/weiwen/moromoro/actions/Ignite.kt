package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("ignite")
data class Ignite(val duration: Int = 80) : Action {
    override fun perform(ctx: Context): Boolean {
        val entity = ctx.entity ?: return false
        entity.fireTicks = duration

        return true
    }
}

