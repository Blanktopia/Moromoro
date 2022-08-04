package me.weiwen.moromoro.actions.effect

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.actions.LocationSelector
import me.weiwen.moromoro.actions.PlayerLocationSelector
import me.weiwen.moromoro.extensions.spawnParticleLine
import org.bukkit.Particle

@Serializable
@SerialName("spawn-particle-line")
data class SpawnParticleLine(
    val from: LocationSelector = PlayerLocationSelector,
    val to: LocationSelector,
    val particle: Particle,
    val interval: Double = 0.2,
) : Action {
    override fun perform(ctx: Context): Boolean {
        val from = from.center(ctx) ?: return false
        val to = to.center(ctx) ?: return false

        from.spawnParticleLine(to, particle, interval)

        return true
    }
}
