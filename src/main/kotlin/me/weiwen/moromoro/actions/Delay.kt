package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.Moromoro

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

