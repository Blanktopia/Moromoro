package me.weiwen.moromoro

import cloud.commandframework.ArgumentDescription
import cloud.commandframework.arguments.standard.IntegerArgument
import cloud.commandframework.arguments.standard.StringArgument
import cloud.commandframework.bukkit.parsers.PlayerArgument
import cloud.commandframework.bukkit.parsers.location.LocationArgument
import cloud.commandframework.execution.CommandExecutionCoordinator
import cloud.commandframework.paper.PaperCommandManager
import me.weiwen.moromoro.hooks.EssentialsHook
import me.weiwen.moromoro.hooks.ShulkerPacksHook
import me.weiwen.moromoro.items.*
import me.weiwen.moromoro.managers.*
import me.weiwen.moromoro.projectiles.ItemProjectileManager
import me.weiwen.moromoro.projectiles.ProjectileManager
import me.weiwen.moromoro.resourcepack.ResourcePackGenerator
import me.weiwen.moromoro.resourcepack.ResourcePackManager
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.function.Function

class Moromoro : JavaPlugin() {
    companion object {
        lateinit var plugin: Moromoro
            private set
    }

    lateinit var config: MoromoroConfig

    private val resourcePackGenerator: ResourcePackGenerator by lazy {
        ResourcePackGenerator(
            this,
            itemManager,
            blockManager
        )
    }
    private val resourcePackManager: ResourcePackManager by lazy { ResourcePackManager(this, resourcePackGenerator) }
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
        config = parseConfig(this)

        experienceBoostManager.enable()
        permanentPotionEffectManager.enable()
        flyInClaimsManager.enable()
        itemProjectileManager.enable()

        if (server.pluginManager.getPlugin("Essentials") != null) {
            essentialsHook.register()
        }

        itemManager.enable()
        recipeManager.enable()
        blockManager.enable()

        projectileManager.enable()
        equippedItemsManager.enable()
        trinketManager.enable()
        itemListener.enable()

        resourcePackManager.enable()

        val plugin = this

        val manager = PaperCommandManager(
            this, CommandExecutionCoordinator.simpleCoordinator(),
            Function.identity(), Function.identity()
        )

        try {
            manager.registerBrigadier()
            manager.registerAsynchronousCompletions()
            plugin.logger.info("Registered commands.")
        } catch (e: Exception) {
            plugin.logger.warning("Failed to initialize Brigadier support: " + e.message)
        }

        manager.commandBuilder("pack", ArgumentDescription.of("Downloads the server resource pack")).let { builder ->
            manager.command(builder.senderType(Player::class.java).permission("moromoro.pack").handler {
                val player = it.sender as Player
                if (!player.hasResourcePack()) {
                    resourcePackManager.send(player)
                }
            })
            manager.command(builder.argument(PlayerArgument.of("player")).permission("moromoro.admin").handler {
                val player: Player = it.get("player")
                it.sender.sendMessage(
                    "${ChatColor.GOLD}${player.name} ${
                        if (player.hasResourcePack()) {
                            "HAS"
                        } else {
                            "DOES NOT HAVE"
                        }
                    } the resource pack enabled."
                )
            })
        }

        manager.commandBuilder("trinkets", ArgumentDescription.of("Opens your trinket bag")).let { builder ->
            manager.command(builder.senderType(Player::class.java).permission("moromoro.trinkets").handler {
                trinketManager.openTrinketInventory(it.sender as Player)
            })
        }

        manager.commandBuilder("moromoro", ArgumentDescription.of("Manages the Moromoro plugin"))
            .permission("moromoro.admin").let { builder ->
                manager.command(
                    builder.literal("items", ArgumentDescription.of("Opens a GUI to spawn custom items"))
                        .senderType(Player::class.java)
                        .handler { itemManager.creativeItemPicker(it.sender as Player) }
                )

                manager.command(
                    builder.literal("drop", ArgumentDescription.of("Drops a custom item at the specified location"))
                        .argument(StringArgument.of("key"))
                        .argument(IntegerArgument.of("amount"))
                        .argument(LocationArgument.of("location"))
                        .handler {
                            val key = it.get<String>("key")
                            val template = itemManager.templates[key] ?: return@handler
                            val item = template.item(key, it.get<Int>("amount"))
                            val location = it.get<Location>("location")
                            location.world.dropItemNaturally(location, item)
                        })

                builder.literal("debug", ArgumentDescription.of("Prints some debug information"))
                    .let { debugBuilder ->
                        manager.command(debugBuilder.handler {
                            it.sender.sendMessage(ChatColor.GOLD.toString() + "${itemManager.keys.size} items, ${blockManager.blockTemplates.size} blocks, ${recipeManager.recipes.size} recipes loaded.")
                        })

                        debugBuilder.literal("blocks").let { builder ->
                            manager.command(builder.handler {
                                val blocks = blockManager.blockTemplates.keys.joinToString(", ")
                                it.sender.sendMessage(ChatColor.GOLD.toString() + "$blocks")
                            })
                            manager.command(builder.argument(StringArgument.of("key")).handler {
                                val template = blockManager.blockTemplates[it.get("key")]
                                if (template != null) {
                                    it.sender.sendMessage(ChatColor.GOLD.toString() + "$template")
                                } else {
                                    it.sender.sendMessage(ChatColor.GOLD.toString() + "No such item.")
                                }
                            })
                        }

                        debugBuilder.literal("items").let { builder ->
                            manager.command(builder.literal("items").handler {
                                val items = itemManager.keys.joinToString(", ")
                                it.sender.sendMessage(ChatColor.GOLD.toString() + "$items")
                            })
                            manager.command(builder.argument(StringArgument.of("key")).handler {
                                val template = itemManager.templates[it.get("key")]
                                if (template != null) {
                                    it.sender.sendMessage(ChatColor.GOLD.toString() + "$template")
                                } else {
                                    it.sender.sendMessage(ChatColor.GOLD.toString() + "No such item.")
                                }
                            })
                        }

                        debugBuilder.literal("recipes").let { builder ->
                            manager.command(builder.literal("recipes").handler {
                                val recipes = recipeManager.recipes.keys.joinToString(", ") { key -> key.key }
                                it.sender.sendMessage(ChatColor.GOLD.toString() + "$recipes")
                            })
                            manager.command(builder.argument(StringArgument.of("key")).handler {
                                val template =
                                    recipeManager.recipes[NamespacedKey(this.config.namespace, it.get("key"))]
                                if (template != null) {
                                    it.sender.sendMessage(ChatColor.GOLD.toString() + "$template")
                                } else {
                                    it.sender.sendMessage(ChatColor.GOLD.toString() + "No such item.")
                                }
                            })
                        }
                    }

                manager.command(builder.literal("reload").handler {
                    equippedItemsManager.disable()
                    config = parseConfig(plugin)
                    itemManager.load()
                    blockManager.load()
                    recipeManager.load()
                    equippedItemsManager.enable()
                    it.sender.sendMessage(ChatColor.GOLD.toString() + "Reloaded configuration!")
                })
            }

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
