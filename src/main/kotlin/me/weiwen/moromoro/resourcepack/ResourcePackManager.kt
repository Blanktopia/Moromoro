package me.weiwen.moromoro.resourcepack

import me.weiwen.moromoro.Moromoro
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerResourcePackStatusEvent

class ResourcePackManager(private val plugin: Moromoro, private val resourcePackGenerator: ResourcePackGenerator) : Listener {
    companion object {
        lateinit var manager: ResourcePackManager
    }

    fun enable() {
        manager = this
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    fun disable() {}

    fun send(player: Player) {
        val url = plugin.config.resourcePackUrl ?: return
        val hash = plugin.config.resourcePackHash
        if (hash == null || hash.isEmpty()) {
            player.setResourcePack(url)
        } else {
            player.setResourcePack(url, hash)
        }
    }

    fun generate() {
        resourcePackGenerator.generate()
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        send(event.player)
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