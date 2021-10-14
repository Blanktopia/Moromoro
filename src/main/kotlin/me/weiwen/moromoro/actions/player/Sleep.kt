package me.weiwen.moromoro.actions.player

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import org.bukkit.ChatColor
import org.bukkit.World

@Serializable
@SerialName("sleep")
object Sleep : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        if (player.world.environment == World.Environment.NETHER || player.world.environment == World.Environment.THE_END) {
            return false
        }
        Moromoro.plugin.server.broadcastMessage(player.displayName + ChatColor.GRAY + " is going to bed. Sweet dreams!")
        skipNight(player.world)
        return true
    }

    private fun skipNight(world: World) {
        world.time = 1000
        if (world.hasStorm()) {
            world.setStorm(false)
        }
    }
}

