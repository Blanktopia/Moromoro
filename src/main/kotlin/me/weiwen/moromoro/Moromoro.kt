package me.weiwen.moromoro

import me.weiwen.moromoro.hooks.EssentialsHook
import me.weiwen.moromoro.listeners.PlayerInteractListener
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin

class Moromoro: JavaPlugin() {
    companion object {
        lateinit var plugin: Moromoro
            private set
    }

    var config: MoromoroConfig = parseConfig(this)

    val itemManager: ItemManager by lazy { ItemManager(this) }
    val itemParser: ItemParser by lazy { ItemParser(this) }

    val permanentPotionEffectManager: PermanentPotionEffectManager by lazy { PermanentPotionEffectManager(this) }

    private val essentialsHook: EssentialsHook by lazy { EssentialsHook(this) }

    override fun onLoad() {
        plugin = this
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(PlayerInteractListener(this), this)

        itemManager.load()
        permanentPotionEffectManager.enable()

        if (server.pluginManager.getPlugin("Essentials") != null) {
            essentialsHook.register()
        }

        val command = getCommand("moromoro")
        command?.setExecutor { sender, _, _, args ->
            when (args[0]) {
                "reload" -> {
                    config = parseConfig(this)
                    itemManager.load()
                    sender.sendMessage(ChatColor.GOLD.toString() + "Reloaded configuration!")
                    true
                }
                else -> false
            }
        }
        command?.setTabCompleter { sender, _, _, args ->
            when (args.size) {
                0 -> listOf("reload")
                else -> listOf()
            }
        }

        logger.info("Monogoto is enabled")
    }

    override fun onDisable() {
        if (server.pluginManager.getPlugin("Essentials") != null) {
            essentialsHook.unregister()
        }

        permanentPotionEffectManager.disable()

        logger.info("Monogoto is disabled")
    }
}
