package me.weiwen.moromoro.actions.clock

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context

@Serializable
@SerialName("repeat")
data class Repeat(val delay: Int = 0, val interval: Int, val count: Int, val actions: List<Action>) : Action {
    override fun perform(ctx: Context): Boolean {
        for (i in 0..count) {
            Moromoro.plugin.server.scheduler.runTaskLater(Moromoro.plugin, { ->
                actions.forEach { it.perform(ctx) }
            }, delay.toLong() + interval * i)
        }
        return true
    }
}

