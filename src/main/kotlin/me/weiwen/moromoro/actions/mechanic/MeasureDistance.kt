package me.weiwen.moromoro.actions.mechanic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Vibration
import org.bukkit.util.Vector
import java.lang.Math.abs
import java.text.DecimalFormat
import java.util.*

@Serializable
@SerialName("measure-distance")
data class MeasureDistance(@SerialName("is-origin") val isOrigin: Boolean = false) : Action {
    companion object {
        val locations: MutableMap<UUID, Location> = mutableMapOf()
    }

    override fun perform(ctx: Context): Boolean {
        val block = ctx.block ?: return false
        val player = ctx.player ?: return false
        val location = locations[player.uniqueId]

        if (!isOrigin && location != null) {
            val distance = DecimalFormat("#.#").format(location.distance(block.location) + 1)
            val d = location.toVector().subtract(block.location.toVector()).add(Vector(1, 1, 1))

            player.sendActionBar(
                "${ChatColor.GOLD}Distance: $distance blocks (x: ${d.blockX}, y: ${d.blockY}, z: ${d.blockZ}, volume: ${
                    abs(
                        d.blockX * d.blockY * d.blockZ
                    )
                })"
            )

            val vibration = Vibration(block.location, Vibration.Destination.BlockDestination(location.block), 20)
            player.spawnParticle(Particle.VIBRATION, block.location, 1, vibration)
        } else {
            player.sendActionBar("${ChatColor.GOLD}Right click another block to measure the distance.")
            locations[player.uniqueId] = block.location
        }
        return true
    }
}

