package me.weiwen.moromoro

import cloud.commandframework.ArgumentDescription
import cloud.commandframework.arguments.standard.IntegerArgument
import cloud.commandframework.arguments.standard.StringArgument
import cloud.commandframework.bukkit.parsers.PlayerArgument
import cloud.commandframework.bukkit.parsers.location.LocationArgument
import cloud.commandframework.execution.CommandExecutionCoordinator
import cloud.commandframework.paper.PaperCommandManager
import me.weiwen.moromoro.blocks.BlockListener
import me.weiwen.moromoro.enchantments.listeners.*
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
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.util.function.Function

class Moromoro(var config: MoromoroConfig) : JavaPlugin() {
    companion object {
        lateinit var plugin: Moromoro private set
    }

    val managers: MutableList<Manager> = mutableListOf()

    override fun onLoad() {
        plugin = this
    }

    override fun onEnable() {
        if (server.pluginManager.getPlugin("Essentials") != null) {
            EssentialsHook.register()
        }

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
            UnenchantableListener,

            // Enchantments
            Beheading,
            Final,
            Frost,
            Harvest,
            NightVision,
            Parry,
            Rush,
            Smelt,
            Sniper,
            Soulbound,
            Spectral,
            Spring,
            Sting,
            Stride
        )

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
            manager.command(builder.senderType(Player::class.java).permission("moromoro.pack").handler { ctx ->
                val player = ctx.sender as Player
                ResourcePackManager.send(player)
            })
        }

        manager.commandBuilder("trinkets", ArgumentDescription.of("Opens your trinket bag")).let { builder ->
            manager.command(builder.senderType(Player::class.java).permission("moromoro.trinkets").handler { ctx ->
                TrinketManager.openTrinketInventory(ctx.sender as Player)
            })
        }

        manager.commandBuilder("moromoro", ArgumentDescription.of("Manages the Moromoro plugin"))
            .permission("moromoro.admin").let { builder ->
                manager.command(
                    builder.literal("shop", ArgumentDescription.of("Opens a shop to a user"))
                        .argument(StringArgument.of("shop", StringArgument.StringMode.QUOTED))
                        .argument(PlayerArgument.optional("player"))
                        .handler { ctx ->
                            val player =
                                ctx.getOptional<Player>("player").orElseGet { ctx.sender as? Player }
                                    ?: return@handler
                            ShopManager.show(player, ctx.get("shop"))
                        })
                manager.command(
                    builder.literal("items", ArgumentDescription.of("Opens a GUI to spawn custom items"))
                        .senderType(Player::class.java)
                        .handler { ItemManager.creativeItemPicker(it.sender as Player) }
                )
                manager.command(
                    builder.literal("drop", ArgumentDescription.of("Drops a custom item at the specified location"))
                        .argument(StringArgument.of("key"))
                        .argument(IntegerArgument.of("amount"))
                        .argument(LocationArgument.of("location"))
                        .handler {
                            val key = it.get<String>("key")
                            val template = ItemManager.templates[key] ?: return@handler
                            val item = template.item(key, it.get<Int>("amount"))
                            val location = it.get<Location>("location")
                            location.world.dropItemNaturally(location, item)
                        })

                builder.literal("debug", ArgumentDescription.of("Prints some debug information"))
                    .let { debugBuilder ->
                        manager.command(debugBuilder.handler { ctx ->
                            ctx.sender.sendMessage(ChatColor.GOLD.toString() + "${ItemManager.keys.size} items, ${BlockManager.blockTemplates.size} blocks, ${RecipeManager.recipes.size} recipes loaded.")
                        })

                        debugBuilder.literal("blocks").let { builder ->
                            manager.command(builder.handler { ctx ->
                                val blocks = BlockManager.blockTemplates.keys.joinToString(", ")
                                ctx.sender.sendMessage(ChatColor.GOLD.toString() + "$blocks")
                            })
                            manager.command(builder.argument(StringArgument.of("key")).handler { ctx ->
                                val template = BlockManager.blockTemplates[ctx.get("key")]
                                if (template != null) {
                                    ctx.sender.sendMessage(ChatColor.GOLD.toString() + "$template")
                                } else {
                                    ctx.sender.sendMessage(ChatColor.GOLD.toString() + "No such item.")
                                }
                            })
                        }
                        debugBuilder.literal("items").let { builder ->
                            manager.command(builder.literal("items").handler { ctx ->
                                val items = ItemManager.keys.joinToString(", ")
                                ctx.sender.sendMessage(ChatColor.GOLD.toString() + "$items")
                            })
                            manager.command(builder.argument(StringArgument.of("key")).handler { ctx ->
                                val template = ItemManager.templates[ctx.get("key")]
                                if (template != null) {
                                    ctx.sender.sendMessage(ChatColor.GOLD.toString() + "$template")
                                } else {
                                    ctx.sender.sendMessage(ChatColor.GOLD.toString() + "No such item.")
                                }
                            })
                        }
                        debugBuilder.literal("recipes").let { builder ->
                            manager.command(builder.literal("recipes").handler { ctx ->
                                val recipes = RecipeManager.recipes.keys.joinToString(", ") { key -> key.key }
                                ctx.sender.sendMessage(ChatColor.GOLD.toString() + "$recipes")
                            })
                            manager.command(builder.argument(StringArgument.of("key")).handler { ctx ->
                                val template =
                                    RecipeManager.recipes[NamespacedKey(this.config.namespace, ctx.get("key"))]
                                if (template != null) {
                                    ctx.sender.sendMessage(ChatColor.GOLD.toString() + "$template")
                                } else {
                                    ctx.sender.sendMessage(ChatColor.GOLD.toString() + "No such item.")
                                }
                            })
                        }
                    }
                builder.literal("reload").let { builder ->
                    manager.command(builder.handler { ctx ->
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
                    })
                    manager.command(builder.literal("config").handler { ctx ->
                        config = parseConfig(Companion.plugin)
                        ctx.sender.sendMessage(ChatColor.GOLD.toString() + "Reloaded config.")
                    })
                    manager.command(builder.literal("items").handler { ctx ->
                        EquippedItemsManager.disable()
                        ItemManager.disable()
                        ItemManager.enable()
                        EquippedItemsManager.enable()
                        ctx.sender.sendMessage(ChatColor.GOLD.toString() + "Reloaded items.")
                    })
                    manager.command(builder.literal("blocks").handler { ctx ->
                        BlockManager.disable()
                        BlockManager.enable()
                        ctx.sender.sendMessage(ChatColor.GOLD.toString() + "Reloaded blocks.")
                    })
                    manager.command(builder.literal("recipes").handler { ctx ->
                        RecipeManager.disable()
                        RecipeManager.enable()
                        ctx.sender.sendMessage(ChatColor.GOLD.toString() + "Reloaded recipes.")
                    })
                    manager.command(builder.literal("shops").handler { ctx ->
                        ShopManager.disable()
                        ShopManager.enable()
                        ctx.sender.sendMessage(ChatColor.GOLD.toString() + "Reloaded shops.")
                    })
                    manager.command(builder.literal("pack").handler { ctx ->
                        ResourcePackManager.generate()
                        ctx.sender.sendMessage(ChatColor.GOLD.toString() + "Generated resource pack!")
                    })
                }
            }

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

    private fun registerEvents(vararg listeners: Listener) {
        listeners.forEach {
            server.pluginManager.registerEvents(it, plugin)
        }
    }
}