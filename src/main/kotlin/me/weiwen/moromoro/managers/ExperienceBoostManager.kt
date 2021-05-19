package me.weiwen.moromoro.managers

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent
import me.weiwen.moromoro.extensions.setExperience
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import java.util.*

data class ExperienceBoost(
    val multiplier: Double,
    val expiry: Long
)

class ExperienceBoostManager(val plugin: JavaPlugin) : Listener {
    companion object {
        lateinit var manager: ExperienceBoostManager
    }

    val experienceBoosts: MutableMap<UUID, ExperienceBoost> = mutableMapOf()

    fun enable() {
        manager = this
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    fun disable() {}

    @EventHandler
    fun onExperiencePickUp(event: PlayerPickupExperienceEvent) {
        val player = event.player
        val experienceBoost = experienceBoosts[player.uniqueId] ?: return

        if (experienceBoost.expiry < System.currentTimeMillis()) {
            experienceBoosts.remove(player.uniqueId)
            return
        }

        event.experienceOrb.setExperience(event.experienceOrb.experience * experienceBoost.multiplier)
        player.sendActionBar("${ChatColor.AQUA}${experienceBoost.multiplier}x EXP")
    }
}

fun Player.addExperienceBoost(multiplier: Double, ticks: Int) {
    val other = ExperienceBoostManager.manager.experienceBoosts.get(uniqueId)
    if (other != null && other.multiplier > multiplier) return

    ExperienceBoostManager.manager.experienceBoosts.put(
        uniqueId,
        ExperienceBoost(multiplier, System.currentTimeMillis() + ticks * 50)
    )
}