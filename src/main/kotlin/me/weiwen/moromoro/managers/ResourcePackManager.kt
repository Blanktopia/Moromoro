package me.weiwen.moromoro.managers

import me.weiwen.moromoro.Moromoro
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerResourcePackStatusEvent

class ResourcePackManager(val plugin: Moromoro) : Listener {
    companion object {
        lateinit var manager: ResourcePackManager
    }

    fun enable() {
        manager = this
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    fun disable() {}

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val url = plugin.config.resourcePackUrl ?: return
        val hash = plugin.config.resourcePackHash ?: return
        event.player.setResourcePack(url, hash)
    }

    @EventHandler
    fun onPlayerResourcePackStatus(event: PlayerResourcePackStatusEvent) {
        if (event.status == PlayerResourcePackStatusEvent.Status.DECLINED) {
            event.player.sendMessage("${ChatColor.GOLD}Activating the resource pack will enhance your experience and is highly recommended. You can also download and apply it manually here: ${plugin.config.resourcePackUrl}")
        }
        if (event.status == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) {
            event.player.sendMessage("${ChatColor.GOLD}Resource pack download failed. You can download and apply the resource pack manually here: ${plugin.config.resourcePackUrl}")
        }
    }
}