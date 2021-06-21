package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.util.Vector
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
            player.sendActionBar("${ChatColor.GOLD}Distance: $distance blocks (x: ${d.x}, y: ${d.y}, z: ${d.z})")

        } else {
            player.sendActionBar("${ChatColor.GOLD}Right click another block to measure the distance.")
            locations[player.uniqueId] = block.location
        }
        return true
    }
}

