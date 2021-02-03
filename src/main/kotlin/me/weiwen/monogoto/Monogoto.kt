package me.weiwen.monogoto

import me.weiwen.monogoto.hooks.EssentialsXHook
import me.weiwen.monogoto.listeners.PlayerInteractListener
import org.bukkit.plugin.java.JavaPlugin

class Monogoto : JavaPlugin() {
    companion object {
        lateinit var plugin: Monogoto
            private set
    }

    val itemManager: ItemManager by lazy { ItemManager(this) }
    val itemParser: ItemParser by lazy { ItemParser(this) }

    private val essentialsxHook: EssentialsXHook by lazy { EssentialsXHook(this) }

    override fun onLoad() {
        plugin = this
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(PlayerInteractListener(this), this)

        itemManager.load()

        if (server.pluginManager.getPlugin("Essentials") != null) {
            essentialsxHook.register()
        }

        logger.info("Monogoto is enabled")
    }

    override fun onDisable() {
        if (server.pluginManager.getPlugin("Essentials") != null) {
            essentialsxHook.unregister()
        }

        logger.info("Monogoto is disabled")
    }
}
