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
import com.mineinabyss.idofront.plugin.registerEvents
import me.weiwen.moromoro.blocks.BlockListener
import me.weiwen.moromoro.equip.EquippedItemsManager
import me.weiwen.moromoro.hooks.EssentialsHook
import me.weiwen.moromoro.items.ItemListener
import me.weiwen.moromoro.items.ItemManager
import me.weiwen.moromoro.items.item
import me.weiwen.moromoro.managers.*
import me.weiwen.moromoro.recipes.RecipeListener
import me.weiwen.moromoro.recipes.RecipeManager
import me.weiwen.moromoro.resourcepack.ResourcePackListener
import me.weiwen.moromoro.resourcepack.ResourcePackManager
import me.weiwen.moromoro.shop.ShopManager
import me.weiwen.moromoro.trinkets.TrinketManager
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.function.Function

class Moromoro : JavaPlugin() {
    companion object {
        lateinit var plugin: Moromoro private set
    }

    lateinit var config: MoromoroConfig

    val managers: MutableList<Manager> = mutableListOf()

    override fun onLoad() {
        plugin = this

        IdofrontPlatforms.load(this, "mineinabyss")
    }

    override fun onEnable() {
        config = parseConfig(this)

        registerManagers(
            ItemManager,
            EquippedItemsManager,
            TrinketManager,
            BlockManager,
            RecipeManager,
            ShopManager,
            ResourcePackManager,
            PermanentPotionEffectManager,
            ProjectileManager,
        )

        registerEvents(
            ResourcePackListener,
            FlyInClaimsListener,
            ExperienceBoostListener,
            ItemListener,
            BlockListener,
            RecipeListener,
        )

        if (server.pluginManager.getPlugin("Essentials") != null) {
            EssentialsHook.register()
        }

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

        manager.command(manager.commandBuilder("pack", ArgumentDescription.of("Downloads the server resource pack"))
        {
            permission = "moromoro.pack"
            senderType<Player>()
            handler { ctx ->
                val player = ctx.sender as Player
                if (!player.hasResourcePack()) {
                    ResourcePackManager.send(player)
                }
            }

            registerCopy {
                literal("force")
                handler { ctx ->
                    val player = ctx.sender as Player
                    ResourcePackManager.send(player)
                }
            }
        })

        manager.command(manager.commandBuilder("trinkets", ArgumentDescription.of("Opens your trinket bag"))
        {
            senderType<Player>()
            handler { ctx ->
                TrinketManager.openTrinketInventory(ctx.sender as Player)
            }
        })

        manager.command(manager.commandBuilder("moromoro", ArgumentDescription.of("Manages the Moromoro plugin"))
        {
            permission = "moromoro.admin"

            registerCopy {
                literal("shop", argumentDescription("Opens a shop to a user"))
                argument(StringArgument.of("shop", StringArgument.StringMode.QUOTED))
                argument(PlayerArgument.optional("player"))
                handler { ctx ->
                    val player = ctx.getOptional<Player>("player").orElseGet { ctx.sender as? Player } ?: return@handler
                    ShopManager.show(player, ctx.get("shop"))
                }
            }

            registerCopy {
                literal("items", argumentDescription("Opens a GUI to spawn custom items"))
                senderType<Player>()
                handler { ctx -> ItemManager.creativeItemPicker(ctx.sender as Player) }
            }

            registerCopy {
                literal("drop", argumentDescription("Drops a custom item at the specified location"))
                argument(StringArgument.of("key"))
                argument(IntegerArgument.of("amount"))
                argument(LocationArgument.of("location"))
                handler { ctx ->
                    val key = ctx.get<String>("key")
                    val template = ItemManager.templates[key] ?: return@handler
                    val item = template.item(key, ctx.get<Int>("amount"))
                    val location = ctx.get<Location>("location")
                    location.world.dropItemNaturally(location, item)
                }
            }

            registerCopy {
                literal("debug", argumentDescription("Prints some debug information"))

                handler { ctx ->
                    ctx.sender.sendMessage(ChatColor.GOLD.toString() + "${ItemManager.keys.size} items, ${BlockManager.blockTemplates.size} blocks, ${RecipeManager.recipes.size} recipes loaded.")
                }

                registerCopy {
                    literal("blocks")
                    handler { ctx ->
                        val blocks = BlockManager.blockTemplates.keys.joinToString(", ")
                        ctx.sender.sendMessage(ChatColor.GOLD.toString() + blocks)
                    }

                    registerCopy {
                        argument(StringArgument.of("key"))
                        handler { ctx ->
                            val template = BlockManager.blockTemplates[ctx.get("key")]
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
                        val items = ItemManager.templates.keys.joinToString(", ")
                        ctx.sender.sendMessage(ChatColor.GOLD.toString() + items)
                    }

                    registerCopy {
                        argument(StringArgument.of("key"))
                        handler { ctx ->
                            val template = ItemManager.templates[ctx.get("key")]
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
                        val recipes = RecipeManager.recipes.keys.joinToString(", ") { key -> key.key }
                        ctx.sender.sendMessage(ChatColor.GOLD.toString() + recipes)
                    }

                    registerCopy {
                        argument(StringArgument.of("key"))
                        handler { ctx ->
                            val template =
                                RecipeManager.recipes[NamespacedKey(config.namespace, ctx.get("key"))]
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
                    EquippedItemsManager.disable()
                    config = parseConfig(Companion.plugin)
                    ShopManager.disable()
                    RecipeManager.disable()
                    BlockManager.disable()
                    ItemManager.disable()
                    ItemManager.enable()
                    BlockManager.enable()
                    RecipeManager.enable()
                    ShopManager.enable()
                    EquippedItemsManager.enable()
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
                        EquippedItemsManager.disable()
                        ItemManager.disable()
                        ItemManager.enable()
                        EquippedItemsManager.enable()
                        ctx.sender.sendMessage(ChatColor.GOLD.toString() + "Reloaded items.")
                    }
                }

                registerCopy {
                    literal("blocks")
                    handler { ctx ->
                        BlockManager.disable()
                        BlockManager.enable()
                        ctx.sender.sendMessage(ChatColor.GOLD.toString() + "Reloaded blocks.")
                    }
                }

                registerCopy {
                    literal("recipes")
                    handler { ctx ->
                        RecipeManager.disable()
                        RecipeManager.enable()
                        ctx.sender.sendMessage(ChatColor.GOLD.toString() + "Reloaded recipes.")
                    }
                }

                registerCopy {
                    literal("shops")
                    handler { ctx ->
                        ShopManager.disable()
                        ShopManager.enable()
                        ctx.sender.sendMessage(ChatColor.GOLD.toString() + "Reloaded shops.")
                    }
                }

                registerCopy {
                    literal("pack")
                    handler { ctx ->
                        ResourcePackManager.generate()
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
            EssentialsHook.unregister()
        }

        managers.reversed().forEach { it.disable() }

        logger.info("Moromoro is disabled")
    }

    private fun registerManagers(vararg managers: Manager) {
        managers.forEach {
            it.enable()
            this.managers.add(it)
        }
    }
}