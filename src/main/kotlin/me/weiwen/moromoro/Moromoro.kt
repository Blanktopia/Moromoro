package me.weiwen.moromoro

import me.weiwen.moromoro.hooks.EssentialsHook
import me.weiwen.moromoro.items.EquippedItemsManager
import me.weiwen.moromoro.items.ItemListener
import me.weiwen.moromoro.items.TrinketManager
import me.weiwen.moromoro.managers.*
import me.weiwen.moromoro.projectiles.ItemProjectileManager
import me.weiwen.moromoro.resourcepack.ResourcePackManager
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class Moromoro : JavaPlugin() {
    companion object {
        lateinit var plugin: Moromoro
            private set
    }

    var config: MoromoroConfig = parseConfig(this)

    private val resourcePackManager: ResourcePackManager by lazy { ResourcePackManager(this) }
    private val itemManager: ItemManager by lazy { ItemManager(this) }
    private val equippedItemsManager: EquippedItemsManager by lazy { EquippedItemsManager(this, itemManager) }
    private val trinketManager: TrinketManager by lazy { TrinketManager(this, itemManager) }
    private val itemListener: ItemListener by lazy { ItemListener(plugin, itemManager, equippedItemsManager, trinketManager) }
    val blockManager: BlockManager by lazy { BlockManager(this, itemManager) }
    private val recipeManager: RecipeManager by lazy { RecipeManager(this, itemManager) }

    private val flyInClaimsManager: FlyInClaimsManager by lazy { FlyInClaimsManager(this) }
    private val permanentPotionEffectManager: PermanentPotionEffectManager by lazy { PermanentPotionEffectManager(this) }
    private val experienceBoostManager: ExperienceBoostManager by lazy { ExperienceBoostManager(this) }
    val itemProjectileManager: ItemProjectileManager by lazy { ItemProjectileManager(this) }

    val essentialsHook: EssentialsHook by lazy { EssentialsHook(this, itemManager) }

    override fun onLoad() {
        plugin = this
    }

    override fun onEnable() {
        experienceBoostManager.enable()
        permanentPotionEffectManager.enable()
        flyInClaimsManager.enable()
        itemProjectileManager.enable()

        itemManager.enable()
        equippedItemsManager.enable()
        trinketManager.enable()
        itemListener.enable()
        blockManager.enable()
        recipeManager.enable()

        if (server.pluginManager.getPlugin("Essentials") != null) {
            essentialsHook.register()
        }

        resourcePackManager.enable()

        val plugin = this

        getCommand("pack")?.let {
            it.setExecutor { sender, _, _, _ ->
                if (sender is Player) {
                    resourcePackManager.send(sender)
                    true
                } else {
                    false
                }
            }
        }

        getCommand("trinkets")?.apply {
            setExecutor { sender, _, _, _ ->
                if (sender is Player) {
                    trinketManager.openTrinketInventory(sender)
                    true
                } else {
                    false
                }
            }
        }

        getCommand("moromoro")?.apply {
            setExecutor { sender, _, _, args ->
                when (args[0]) {
                    "items" -> {
                        if (sender is Player) {
                            itemManager.creativeItemPicker(sender)
                            true
                        } else {
                            false
                        }
                    }
                    "trinkets" -> {
                        if (sender is Player) {
                            trinketManager.openTrinketInventory(sender)
                            true
                        } else {
                            false
                        }
                    }
                    "rp" -> {
                        if (sender is Player) {
                            resourcePackManager.send(sender)
                            true
                        } else {
                            false
                        }
                    }
                    "debug" -> {
                        if (args.size == 1) {
                            sender.sendMessage(ChatColor.GOLD.toString() + "${itemManager.keys.size} items, ${recipeManager.recipes.size} recipes loaded.")
                            true
                        } else {
                            val template = itemManager.templates[args[1]]
                            if (template != null) {
                                sender.sendMessage(ChatColor.GOLD.toString() + "$template")
                                true
                            } else {
                                sender.sendMessage(ChatColor.GOLD.toString() + "No such item.")
                                false
                            }
                        }
                    }
                    "reload" -> {
                        equippedItemsManager.disable()
                        if (args.size == 1) {
                            config = parseConfig(plugin)
                            itemManager.load()
                            blockManager.load()
                            recipeManager.load()
                        } else {
                            when (args[1]) {
                                "config" -> parseConfig(plugin)
                                "items" -> itemManager.load()
                                "blocks" -> blockManager.load()
                                "recipes" -> recipeManager.load()
                                else -> {
                                    config = parseConfig(plugin)
                                    itemManager.load()
                                    recipeManager.load()
                                }
                            }
                        }
                        equippedItemsManager.enable()
                        sender.sendMessage(ChatColor.GOLD.toString() + "Reloaded configuration!")
                        true
                    }
                    else -> false
                }
            }
            setTabCompleter { _, _, _, args ->
                when (args.size) {
                    0 -> listOf("reload", "rp", "debug", "trinkets", "items")
                    else -> listOf()
                }
            }
        }

        // Hotfix: reload to allow recursive
        config = parseConfig(this)
        itemManager.load()
        recipeManager.load()

        logger.info("Moromoro is enabled")
    }

    override fun onDisable() {
        if (server.pluginManager.getPlugin("Essentials") != null) {
            essentialsHook.unregister()
        }

        resourcePackManager.disable()
        itemListener.disable()
        recipeManager.disable()
        blockManager.disable()
        itemManager.disable()
        trinketManager.disable()
        equippedItemsManager.disable()

        itemProjectileManager.disable()
        flyInClaimsManager.disable()
        permanentPotionEffectManager.disable()
        experienceBoostManager.disable()

        logger.info("Moromoro is disabled")
    }
}
