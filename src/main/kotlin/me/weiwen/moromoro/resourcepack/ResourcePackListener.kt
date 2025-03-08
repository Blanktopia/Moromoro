package me.weiwen.moromoro.resourcepack

import me.weiwen.moromoro.Moromoro.Companion.plugin
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerResourcePackStatusEvent

object ResourcePackListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        ResourcePackManager.send(event.player, false)
    }

    @EventHandler
    fun onPlayerResourcePackStatus(event: PlayerResourcePackStatusEvent) {
        if (event.status == PlayerResourcePackStatusEvent.Status.DECLINED) {
            event.player.sendMessage("${ChatColor.GOLD}Activating the resource pack will enhance your experience and is highly recommended. You can also download and apply it manually here: ${ChatColor.BLUE}${ChatColor.UNDERLINE}${plugin.config.resourcePackUrl}")
        }
        if (event.status == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) {
            event.player.sendMessage("${ChatColor.GOLD}Resource pack download failed. You can download and apply the resource pack manually here: ${ChatColor.BLUE}${ChatColor.UNDERLINE}${plugin.config.resourcePackUrl}")
        }
    }
}