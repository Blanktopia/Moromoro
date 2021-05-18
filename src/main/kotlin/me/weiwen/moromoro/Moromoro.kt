package me.weiwen.moromoro

import me.weiwen.moromoro.hooks.EssentialsHook
import me.weiwen.moromoro.listeners.PlayerInteractListener
import org.bukkit.plugin.java.JavaPlugin

class Moromoro: JavaPlugin() {
    companion object {
        lateinit var plugin: Moromoro
            private set
    }

    val config: MoromoroConfig by lazy { parseConfig(this) }

    val itemManager: ItemManager by lazy { ItemManager(this) }
    val itemParser: ItemParser by lazy { ItemParser(this) }

    private val essentialsHook: EssentialsHook by lazy { EssentialsHook(this) }

    override fun onLoad() {
        plugin = this
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(PlayerInteractListener(this), this)

        itemManager.load()

        if (server.pluginManager.getPlugin("Essentials") != null) {
            essentialsHook.register()
        }

        logger.info("Monogoto is enabled")
    }

    override fun onDisable() {
        if (server.pluginManager.getPlugin("Essentials") != null) {
            essentialsHook.unregister()
        }

        logger.info("Monogoto is disabled")
    }
}
