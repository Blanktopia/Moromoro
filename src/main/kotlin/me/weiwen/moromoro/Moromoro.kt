package me.weiwen.moromoro

import me.weiwen.moromoro.hooks.EssentialsHook
import me.weiwen.moromoro.listeners.PlayerListener
import me.weiwen.moromoro.managers.*
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin

class Moromoro: JavaPlugin() {
    companion object {
        lateinit var plugin: Moromoro
            private set
    }

    var config: MoromoroConfig = parseConfig(this)

    val equippedItemsManager: EquippedItemsManager by lazy { EquippedItemsManager(this) }
    val flyItemsManager: FlyInClaimsManager by lazy { FlyInClaimsManager(this) }
    val itemManager: ItemManager by lazy { ItemManager(this) }
    val recipeManager: RecipeManager by lazy { RecipeManager(this) }
    val permanentPotionEffectManager: PermanentPotionEffectManager by lazy { PermanentPotionEffectManager(this) }

    val essentialsHook: EssentialsHook by lazy { EssentialsHook(this) }

    override fun onLoad() {
        plugin = this
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(PlayerListener(this), this)

        itemManager.enable()
        recipeManager.enable()
        equippedItemsManager.enable()
        permanentPotionEffectManager.enable()
        flyItemsManager.enable()

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
        command?.setTabCompleter { _, _, _, args ->
            when (args.size) {
                0 -> listOf("reload")
                else -> listOf()
            }
        }

        logger.info("Moromoro is enabled")
    }

    override fun onDisable() {
        if (server.pluginManager.getPlugin("Essentials") != null) {
            essentialsHook.unregister()
        }

        flyItemsManager.disable()
        permanentPotionEffectManager.disable()
        equippedItemsManager.disable()
        recipeManager.disable()
        itemManager.disable()

        logger.info("Moromoro is disabled")
    }
}
