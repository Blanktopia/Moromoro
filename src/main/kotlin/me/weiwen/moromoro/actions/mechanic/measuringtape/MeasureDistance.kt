package me.weiwen.moromoro.actions.mechanic.measuringtape

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.extensions.spawnParticleCuboid
import me.weiwen.moromoro.extensions.spawnParticleLine
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.util.Vector
import java.text.DecimalFormat
import java.util.*
import kotlin.math.abs

@Serializable
@SerialName("measure-distance")
data class MeasureDistance(@SerialName("is-origin") val isOrigin: Boolean = false) : Action {
    companion object {
        val locations: MutableMap<UUID, Location> = mutableMapOf()
    }

    override fun perform(ctx: Context): Boolean {
        val block = ctx.block ?: return false
        val player = ctx.player ?: return false
        val from = locations[player.uniqueId]


        if (!isOrigin && from != null) {
            if (from.world != block.world) return false

            val distance = DecimalFormat("#.#").format(from.distance(block.location) + 1)
            val displacement = from.toVector().subtract(block.location.toVector())
            val d = Vector(abs(displacement.x) + 1.0, abs(displacement.y) + 1.0, abs(displacement.z) + 1.0)

            player.sendActionBar(
                "${ChatColor.GOLD}Distance: $distance blocks (${d.blockX}x${d.blockZ}x${d.blockY} = ${
                    abs(
                        d.blockX * d.blockY * d.blockZ
                    )
                })"
            )

            (0..20).forEach { i ->
                Bukkit.getScheduler().scheduleSyncDelayedTask(Moromoro.plugin, {
                    from.block.spawnParticleCuboid(block, Particle.END_ROD, 0.5)
                    from.spawnParticleLine(block.location, Particle.END_ROD, 0.5)
                }, (i * 3).toLong())
            }

        } else {
            player.sendActionBar("${ChatColor.GOLD}Right click another block to measure the distance.")
            locations[player.uniqueId] = block.location
        }
        return true
    }
}

