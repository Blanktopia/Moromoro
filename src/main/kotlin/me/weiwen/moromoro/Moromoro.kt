package me.weiwen.moromoro

import me.weiwen.moromoro.hooks.EssentialsHook
import me.weiwen.moromoro.hooks.ShulkerPacksHook
import me.weiwen.moromoro.items.EquippedItemsManager
import me.weiwen.moromoro.items.ItemListener
import me.weiwen.moromoro.items.ItemManager
import me.weiwen.moromoro.items.TrinketManager
import me.weiwen.moromoro.managers.*
import me.weiwen.moromoro.projectiles.ItemProjectileManager
import me.weiwen.moromoro.projectiles.ProjectileManager
import me.weiwen.moromoro.resourcepack.ResourcePackManager
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class Moromoro : JavaPlugin() {
    companion object {
        lateinit var plugin: Moromoro
            private set
    }

    var config: MoromoroConfig = parseConfig(this)

    private val resourcePackManager: ResourcePackManager by lazy { ResourcePackManager(this) }
    val itemManager: ItemManager by lazy { ItemManager(this) }
    private val equippedItemsManager: EquippedItemsManager by lazy { EquippedItemsManager(this, itemManager) }
    private val trinketManager: TrinketManager by lazy { TrinketManager(this, itemManager) }
    private val itemListener: ItemListener by lazy {
        ItemListener(
            plugin,
            itemManager,
            equippedItemsManager,
            trinketManager,
            projectileManager
        )
    }
    val blockManager: BlockManager by lazy { BlockManager(this, itemManager) }
    private val recipeManager: RecipeManager by lazy { RecipeManager(this, itemManager) }

    private val flyInClaimsManager: FlyInClaimsManager by lazy { FlyInClaimsManager(this) }
    private val permanentPotionEffectManager: PermanentPotionEffectManager by lazy { PermanentPotionEffectManager(this) }
    private val experienceBoostManager: ExperienceBoostManager by lazy { ExperienceBoostManager(this) }
    val itemProjectileManager: ItemProjectileManager by lazy { ItemProjectileManager(this) }
    val projectileManager: ProjectileManager by lazy { ProjectileManager(this, itemManager) }

    val essentialsHook: EssentialsHook by lazy { EssentialsHook(this, itemManager) }
    val shulkerPacksHook: ShulkerPacksHook? by lazy {
        if (server.pluginManager.getPlugin("ShulkerPacks") != null) {
            ShulkerPacksHook()
        } else {
            null
        }
    }

    override fun onLoad() {
        plugin = this
    }

    override fun onEnable() {
        experienceBoostManager.enable()
        permanentPotionEffectManager.enable()
        flyInClaimsManager.enable()
        itemProjectileManager.enable()

        itemManager.enable()
        projectileManager.enable()
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
                    "drop" -> {
                        if (args.size == 6) {
                            val key = args[1]
                            val template = itemManager.templates[key] ?: return@setExecutor false
                            val item = template.item(key, args[2].toInt())
                            val location = Location(
                                server.getWorld(args[3]),
                                args[4].toDouble(),
                                args[5].toDouble(),
                                args[6].toDouble()
                            )
                            location.world.dropItemNaturally(location, item)
                            true
                        } else {
                            false
                        }
                    }
                    "debug" -> {
                        if (args.size == 1) {
                            sender.sendMessage(ChatColor.GOLD.toString() + "${itemManager.keys.size} items, ${blockManager.blockTemplates.size} blocks, ${recipeManager.recipes.size} recipes loaded.")
                            true
                        } else if (args.size == 2) {
                            if (args[1] == "blocks") {
                                val blocks = blockManager.blockTemplates.keys.joinToString(", ")
                                sender.sendMessage(ChatColor.GOLD.toString() + "$blocks")
                                true
                            } else if (args[1] == "items") {
                                val items = itemManager.templates.keys.joinToString(", ")
                                sender.sendMessage(ChatColor.GOLD.toString() + "$items")
                                true
                            } else {
                                false
                            }
                        } else if (args.size == 3) {
                            if (args[1] == "blocks") {
                                val template = blockManager.blockTemplates[args[1]]
                                if (template != null) {
                                    sender.sendMessage(ChatColor.GOLD.toString() + "$template")
                                    true
                                } else {
                                    sender.sendMessage(ChatColor.GOLD.toString() + "No such item.")
                                    false
                                }
                            } else if (args[1] == "items") {
                                val template = itemManager.templates[args[1]]
                                if (template != null) {
                                    sender.sendMessage(ChatColor.GOLD.toString() + "$template")
                                    true
                                } else {
                                    sender.sendMessage(ChatColor.GOLD.toString() + "No such item.")
                                    false
                                }
                            } else {
                                false
                            }
                        } else {
                            false
                        }
                    }
                    "reload" -> {
                        equippedItemsManager.disable()
                        config = parseConfig(plugin)
                        itemManager.load()
                        blockManager.load()
                        recipeManager.load()
                        equippedItemsManager.enable()
                        sender.sendMessage(ChatColor.GOLD.toString() + "Reloaded configuration!")
                        true
                    }
                    else -> false
                }
            }
            setTabCompleter { _, _, _, args ->
                when (args.size) {
                    0 -> listOf("reload", "debug", "items")
                    else -> listOf()
                }
            }
        }

        // Hotfix: reload to allow recursive
        config = parseConfig(this)
        itemManager.load()
        recipeManager.load()
        blockManager.load()

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
        projectileManager.disable()
        trinketManager.disable()
        equippedItemsManager.disable()

        itemProjectileManager.disable()
        flyInClaimsManager.disable()
        permanentPotionEffectManager.disable()
        experienceBoostManager.disable()

        logger.info("Moromoro is disabled")
    }
}
