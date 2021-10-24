package me.weiwen.moromoro.actions.mechanic.measuringtape

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.extensions.spawnParticleCuboid
import org.bukkit.Particle

@Serializable
@SerialName("measure-distance-tick")
object MeasureDistanceTick : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false

        val origin = MeasureDistance.locations[player.uniqueId] ?: return false

        origin.block.spawnParticleCuboid(origin.block, Particle.END_ROD, 0.5)

        return true
    }
}

