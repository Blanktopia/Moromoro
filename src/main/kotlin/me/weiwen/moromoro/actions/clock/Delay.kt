package me.weiwen.moromoro.actions.clock

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context

@Serializable
@SerialName("delay")
data class Delay(val ticks: Int, val actions: List<Action>) : Action {
    override fun perform(ctx: Context): Boolean {
        Moromoro.plugin.server.scheduler.runTaskLater(Moromoro.plugin, { ->
            actions.forEach { it.perform(ctx) }
        }, ticks.toLong())
        return true
    }
}

