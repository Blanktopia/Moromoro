package me.weiwen.moromoro

import cloud.commandframework.ArgumentDescription
import cloud.commandframework.arguments.standard.IntegerArgument
import cloud.commandframework.arguments.standard.StringArgument
import cloud.commandframework.bukkit.parsers.PlayerArgument
import cloud.commandframework.bukkit.parsers.location.LocationArgument
import cloud.commandframework.execution.CommandExecutionCoordinator
import cloud.commandframework.kotlin.extension.argumentDescription
import cloud.commandframework.kotlin.extension.command
import cloud.commandframework.kotlin.extension.commandBuilder
import cloud.commandframework.paper.PaperCommandManager
import com.mineinabyss.idofront.platforms.IdofrontPlatforms
import me.weiwen.moromoro.hooks.EssentialsHook
import me.weiwen.moromoro.hooks.ShulkerPacksHook
import me.weiwen.moromoro.items.*
import me.weiwen.moromoro.managers.*
import me.weiwen.moromoro.projectiles.ItemProjectileManager
import me.weiwen.moromoro.projectiles.ProjectileManager
import me.weiwen.moromoro.resourcepack.ResourcePackGenerator
import me.weiwen.moromoro.resourcepack.ResourcePackManager
import me.weiwen.moromoro.shop.ShopManager
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
    private val shopManager: ShopManager by lazy { ShopManager(this) }

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

        IdofrontPlatforms.load(this, "mineinabyss")
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
        shopManager.enable()

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

        manager.command(manager.commandBuilder("pack", ArgumentDescription.of("Downloads the server resource pack")) {
            permission = "moromoro.pack"
            senderType<Player>()
            handler { ctx ->
                val player = ctx.sender as Player
                if (!player.hasResourcePack()) {
                    resourcePackManager.send(player)
                }
            }

            registerCopy {
                literal("force")
                handler { ctx ->
                    val player = ctx.sender as Player
                    resourcePackManager.send(player)
                }
            }
        })

        manager.command(manager.commandBuilder("trinkets", ArgumentDescription.of("Opens your trinket bag")) {
            senderType<Player>()
            handler { ctx ->
                trinketManager.openTrinketInventory(ctx.sender as Player)
            }
        })

        manager.command(manager.commandBuilder("moromoro", ArgumentDescription.of("Manages the Moromoro plugin")) {
            permission = "moromoro.admin"

            registerCopy {
                literal("shop", argumentDescription("Opens a shop to a user"))
                argument(StringArgument.of("shop", StringArgument.StringMode.QUOTED))
                argument(PlayerArgument.optional("player"))
                handler { ctx ->
                    val player = ctx.getOptional<Player>("player").orElseGet { ctx.sender as? Player } ?: return@handler
                    shopManager.show(player, ctx.get("shop"))
                }
            }

            registerCopy {
                literal("items", argumentDescription("Opens a GUI to spawn custom items"))
                senderType<Player>()
                handler { ctx -> itemManager.creativeItemPicker(ctx.sender as Player) }
            }

            registerCopy {
                literal("drop", argumentDescription("Drops a custom item at the specified location"))
                argument(StringArgument.of("key"))
                argument(IntegerArgument.of("amount"))
                argument(LocationArgument.of("location"))
                handler { ctx ->
                    val key = ctx.get<String>("key")
                    val template = itemManager.templates[key] ?: return@handler
                    val item = template.item(key, ctx.get<Int>("amount"))
                    val location = ctx.get<Location>("location")
                    location.world.dropItemNaturally(location, item)
                }
            }

            registerCopy {
                literal("debug", argumentDescription("Prints some debug information"))

                handler { ctx ->
                    ctx.sender.sendMessage(ChatColor.GOLD.toString() + "${itemManager.keys.size} items, ${blockManager.blockTemplates.size} blocks, ${recipeManager.recipes.size} recipes loaded.")
                }

                registerCopy {
                    literal("blocks")
                    handler { ctx ->
                        val blocks = blockManager.blockTemplates.keys.joinToString(", ")
                        ctx.sender.sendMessage(ChatColor.GOLD.toString() + blocks)
                    }

                    registerCopy {
                        argument(StringArgument.of("key"))
                        handler { ctx ->
                            val template = blockManager.blockTemplates[ctx.get("key")]
                            if (template != null) {
                                ctx.sender.sendMessage(ChatColor.GOLD.toString() + "$template")
                            } else {
                                ctx.sender.sendMessage(ChatColor.GOLD.toString() + "No such item.")
                            }
                        }
                    }
                }

                registerCopy {
                    literal("items")
                    handler { ctx ->
                        val items = itemManager.templates.keys.joinToString(", ")
                        ctx.sender.sendMessage(ChatColor.GOLD.toString() + items)
                    }

                    registerCopy {
                        argument(StringArgument.of("key"))
                        handler { ctx ->
                            val template = itemManager.templates[ctx.get("key")]
                            if (template != null) {
                                ctx.sender.sendMessage(ChatColor.GOLD.toString() + "$template")
                            } else {
                                ctx.sender.sendMessage(ChatColor.GOLD.toString() + "No such item.")
                            }
                        }
                    }
                }

                registerCopy {
                    literal("recipes")
                    handler { ctx ->
                        val recipes = recipeManager.recipes.keys.joinToString(", ") { key -> key.key }
                        ctx.sender.sendMessage(ChatColor.GOLD.toString() + recipes)
                    }

                    registerCopy {
                        argument(StringArgument.of("key"))
                        handler { ctx ->
                            val template =
                                recipeManager.recipes[NamespacedKey(config.namespace, ctx.get("key"))]
                            if (template != null) {
                                ctx.sender.sendMessage(ChatColor.GOLD.toString() + "$template")
                            } else {
                                ctx.sender.sendMessage(ChatColor.GOLD.toString() + "No such item.")
                            }
                        }
                    }
                }
            }

            registerCopy {
                literal("reload")
                handler { ctx ->
                    equippedItemsManager.disable()
                    config = parseConfig(Companion.plugin)
                    itemManager.load()
                    blockManager.load()
                    recipeManager.load()
                    shopManager.load()
                    equippedItemsManager.enable()
                    ctx.sender.sendMessage(ChatColor.GOLD.toString() + "Reloaded configuration!")
                }

                registerCopy {
                    literal("config")
                    handler { ctx ->
                        config = parseConfig(Companion.plugin)
                        ctx.sender.sendMessage(ChatColor.GOLD.toString() + "Reloaded config.")
                    }
                }

                registerCopy {
                    literal("items")
                    handler { ctx ->
                        equippedItemsManager.disable()
                        itemManager.load()
                        equippedItemsManager.enable()
                        ctx.sender.sendMessage(ChatColor.GOLD.toString() + "Reloaded items.")
                    }
                }

                registerCopy {
                    literal("blocks")
                    handler { ctx ->
                        blockManager.load()
                        ctx.sender.sendMessage(ChatColor.GOLD.toString() + "Reloaded blocks.")
                    }
                }

                registerCopy {
                    literal("recipes")
                    handler { ctx ->
                        recipeManager.load()
                        ctx.sender.sendMessage(ChatColor.GOLD.toString() + "Reloaded recipes.")
                    }
                }

                registerCopy {
                    literal("shops")
                    handler { ctx ->
                        shopManager.load()
                        ctx.sender.sendMessage(ChatColor.GOLD.toString() + "Reloaded shops.")
                    }
                }

                registerCopy {
                    literal("pack")
                    handler { ctx ->
                        resourcePackManager.generate()
                        ctx.sender.sendMessage(ChatColor.GOLD.toString() + "Generated resource pack!")
                    }
                }
            }
        }
        )

        logger.info("Moromoro is enabled")
    }

    override fun onDisable() {
        if (server.pluginManager.getPlugin("Essentials") != null) {
            essentialsHook.unregister()
        }

        resourcePackManager.disable()
        itemListener.disable()
        shopManager.disable()
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
