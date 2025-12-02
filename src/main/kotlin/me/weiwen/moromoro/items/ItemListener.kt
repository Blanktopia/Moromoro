package me.weiwen.moromoro.items

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import com.destroystokyo.paper.event.player.PlayerJumpEvent
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.Moromoro.Companion.plugin
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.actions.Trigger
import me.weiwen.moromoro.actions.condition.InputKey
import me.weiwen.moromoro.actions.condition.lastPressed
import me.weiwen.moromoro.equip.EquippedItemsManager
import me.weiwen.moromoro.extensions.canBuildAt
import me.weiwen.moromoro.extensions.customItemKey
import me.weiwen.moromoro.extensions.isReallyInteractable
import me.weiwen.moromoro.managers.ProjectileManager
import me.weiwen.moromoro.trinkets.TrinketManager
import me.weiwen.moromoro.types.CustomEquipmentSlot
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.entity.ThrowableProjectile
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.*
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.logging.Level

object ItemListener : Listener {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    fun onEntityShootBow(event: EntityShootBowEvent) {
        val item = event.bow ?: return
        val key = item.customItemKey ?: return

        val persistentData = event.projectile.persistentDataContainer
        persistentData.set(NamespacedKey(Moromoro.plugin.config.namespace, "type"), PersistentDataType.STRING, key)

        val triggers = ItemManager.triggers[key] ?: return

        val ctx = Context(
            event,
            event.entity as? Player,
            null,
            null,
            null,
            null,
            event.projectile as? Projectile,
        )

        triggers[Trigger.PROJECTILE_LAUNCH]?.forEach { it.perform(ctx) }

        triggers[Trigger.PROJECTILE_TICK]?.let {
            (event.projectile as? Projectile)?.let { projectile ->
                ProjectileManager.register(projectile, (event.entity as? Player)?.uniqueId, key)
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    fun onThrowableProjectileLaunch(event: ProjectileLaunchEvent) {
        val projectile = event.entity as? ThrowableProjectile ?: return
        val item = projectile.item
        val key = item.customItemKey ?: return

        val persistentData = projectile.persistentDataContainer
        persistentData.set(NamespacedKey(Moromoro.plugin.config.namespace, "type"), PersistentDataType.STRING, key)

        val triggers = ItemManager.triggers[key] ?: return

        val ctx = Context(
            event,
            projectile.shooter as? Player,
            (projectile as? ThrowableProjectile)?.item,
            null,
            null,
            null,
            projectile,
        )

        triggers[Trigger.PROJECTILE_LAUNCH]?.forEach { it.perform(ctx) }

        triggers[Trigger.PROJECTILE_TICK]?.let {
            ProjectileManager.register(projectile, (projectile.shooter as? Player)?.uniqueId, key)
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    fun onProjectileHit(event: ProjectileHitEvent) {
        val key = event.entity.customItemKey ?: return

        val triggers = ItemManager.triggers[key] ?: return

        val ctx = Context(
            event,
            event.entity.shooter as? Player,
            (event.entity as? ThrowableProjectile)?.item,
            event.hitEntity,
            event.hitBlock,
            event.hitBlockFace,
            event.entity,
        )

        triggers[Trigger.PROJECTILE_HIT]?.forEach { it.perform(ctx) }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val item = when (event.hand) {
            EquipmentSlot.HAND -> event.player.inventory.itemInMainHand
            EquipmentSlot.OFF_HAND -> event.player.inventory.itemInOffHand
            else -> return
        }

        val key = item.customItemKey ?: return

        // Cancel if interacting with a block
        if (event.useInteractedBlock() != Event.Result.DENY && event.action == Action.RIGHT_CLICK_BLOCK && !event.player.isSneaking) {
            val blockType = event.clickedBlock?.type
            if (blockType?.isReallyInteractable == true) {
                event.setUseItemInHand(Event.Result.DENY)
                return
            }
        }

        if (event.useItemInHand() == Event.Result.DENY) {
            return
        }

        // Prevent double interaction
        if (event.action == Action.RIGHT_CLICK_BLOCK && event.useInteractedBlock() == Event.Result.DENY) {
            event.setUseItemInHand(Event.Result.DENY)
            return
        }

        val ctx = Context(
            event,
            event.player,
            item,
            null,
            event.clickedBlock,
            event.blockFace
        )

        var didAction = false

        val triggers = ItemManager.triggers[key] ?: return
        when (event.action) {
            Action.LEFT_CLICK_BLOCK -> {
                if (!event.player.canBuildAt(event.clickedBlock?.location ?: return)) {
                    event.setUseItemInHand(Event.Result.DENY)
                    return
                }
                triggers[Trigger.LEFT_CLICK_BLOCK]?.forEach { it.perform(ctx); didAction = true }
                triggers[Trigger.LEFT_CLICK]?.forEach { it.perform(ctx); didAction = true }
            }
            Action.RIGHT_CLICK_BLOCK -> {
                triggers[Trigger.RIGHT_CLICK_BLOCK]?.forEach { it.perform(ctx); didAction = true }
                triggers[Trigger.RIGHT_CLICK]?.forEach { it.perform(ctx); didAction = true }
            }
            Action.LEFT_CLICK_AIR -> {
                triggers[Trigger.LEFT_CLICK_AIR]?.forEach { it.perform(ctx); didAction = true }
                triggers[Trigger.LEFT_CLICK]?.forEach { it.perform(ctx); didAction = true }
            }
            Action.RIGHT_CLICK_AIR -> {
                triggers[Trigger.RIGHT_CLICK_AIR]?.forEach { it.perform(ctx); didAction = true }
                triggers[Trigger.RIGHT_CLICK]?.forEach { it.perform(ctx); didAction = true }
            }
            Action.PHYSICAL -> return
        }

        if (ctx.isCancelled) {
            event.setUseItemInHand(Event.Result.DENY)
        }

        if (ctx.removeItem) {
            event.hand?.let { removeOne(event.player, it) }
            return
        }

        // Equip trinket
        if (!didAction && (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK)) {
            val key = item.customItemKey
            val template = ItemManager.templates[key]
            if (template != null && template.slots.contains(CustomEquipmentSlot.TRINKET)) {
                if (TrinketManager.equipTrinket(event.player, item)) {
                    when (event.hand) {
                        EquipmentSlot.HAND -> event.player.inventory.setItemInMainHand(null)
                        EquipmentSlot.OFF_HAND -> event.player.inventory.setItemInOffHand(null)
                        else -> {}
                    }
                }
                plugin.server.scheduler.scheduleSyncDelayedTask(plugin, {
                    TrinketManager.openTrinketInventory(event.player)
                }, 1)

                event.setUseItemInHand(Event.Result.DENY)
                event.setUseInteractedBlock(Event.Result.DENY)
                return
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        val item = when (event.hand) {
            EquipmentSlot.HAND -> event.player.inventory.itemInMainHand
            EquipmentSlot.OFF_HAND -> event.player.inventory.itemInOffHand
            else -> return
        }

        val key = item.customItemKey ?: return
        val triggers = ItemManager.triggers[key] ?: return

        val ctx = Context(
            event,
            event.player,
            item,
            event.rightClicked,
            null,
            null
        )

        triggers[Trigger.RIGHT_CLICK_ENTITY]?.forEach { it.perform(ctx) }
        triggers[Trigger.RIGHT_CLICK]?.forEach { it.perform(ctx) }

        event.isCancelled = ctx.isCancelled

        if (ctx.removeItem) {
            removeOne(event.player, event.hand)
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onEntityDamageByPlayer(event: EntityDamageByEntityEvent) {
        if (event.cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return

        val player = event.damager as? Player ?: return
        val item = player.inventory.itemInMainHand

        val key = item.customItemKey ?: return
        val triggers = ItemManager.triggers[key] ?: return

        val ctx = Context(
            event,
            player,
            item,
            event.entity,
            null,
            null
        )

        triggers[Trigger.DAMAGE_ENTITY]?.forEach { it.perform(ctx) }

        event.isCancelled = ctx.isCancelled

        if (ctx.removeItem) {
            player.inventory.setItemInMainHand(removeOne(item))
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        val item = event.player.inventory.itemInMainHand

        val key = item.customItemKey ?: return
        val triggers = ItemManager.triggers[key] ?: return

        val ctx = Context(
            event,
            event.player,
            item,
            null,
            event.block,
            null
        )

        triggers[Trigger.BREAK_BLOCK]?.forEach { it.perform(ctx) }

        event.isCancelled = ctx.isCancelled

        if (ctx.removeItem) {
            event.player.inventory.setItemInMainHand(removeOne(item))
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent) {
        val item = event.player.inventory.itemInMainHand

        val key = item.customItemKey ?: return
        val triggers = ItemManager.triggers[key] ?: return

        val ctx = Context(
            event,
            event.player,
            item,
            null,
            event.blockPlaced,
            null
        )

        triggers[Trigger.PLACE_BLOCK]?.forEach { it.perform(ctx) }

        event.isCancelled = ctx.isCancelled

        if (ctx.removeItem) {
            event.player.inventory.setItemInMainHand(removeOne(item))
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPlayerFish(event: PlayerFishEvent) {
        val item = event.player.inventory.itemInMainHand

        val key = item.customItemKey ?: return
        val triggers = ItemManager.triggers[key] ?: return

        val ctx = Context(
            event,
            event.player,
            item,
            event.caught,
            null,
            null,
            event.hook
        )

        val trigger = when (event.state) {
            PlayerFishEvent.State.FISHING -> {
                val persistentData = event.hook.persistentDataContainer
                persistentData.set(
                    NamespacedKey(plugin.config.namespace, "type"),
                    PersistentDataType.STRING,
                    key
                )

                Trigger.FISHING
            }
            PlayerFishEvent.State.CAUGHT_FISH -> Trigger.FISH_CAUGHT_FISH
            PlayerFishEvent.State.CAUGHT_ENTITY -> Trigger.FISH_CAUGHT_ENTITY
            PlayerFishEvent.State.IN_GROUND -> Trigger.FISH_IN_GROUND
            PlayerFishEvent.State.FAILED_ATTEMPT -> Trigger.FISH_FAILED_ATTEMPT
            PlayerFishEvent.State.REEL_IN -> Trigger.FISH_REEL_IN
            PlayerFishEvent.State.BITE -> Trigger.FISH_BITE
            PlayerFishEvent.State.LURED -> Trigger.FISH_LURED
        }

        triggers[trigger]?.forEach { it.perform(ctx) }

        event.isCancelled = ctx.isCancelled

        if (ctx.removeItem) {
            event.player.inventory.setItemInMainHand(item.subtract(1))
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPlayerItemConsume(event: PlayerItemConsumeEvent) {
        val item = event.item

        val key = item.customItemKey ?: return
        val triggers = ItemManager.triggers[key] ?: return

        val ctx = Context(
            event,
            event.player,
            item,
            null,
            null,
            null
        )

        triggers[Trigger.CONSUME]?.forEach { it.perform(ctx) }

        event.isCancelled = ctx.isCancelled

        if (ctx.removeItem) {
            plugin.logger.log(
                Level.WARNING,
                "Attempting to remove item during PlayerItemConsumeEvent. This is not currently possible."
            )
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        val item = event.itemDrop.itemStack

        val key = item.customItemKey ?: return
        val triggers = ItemManager.triggers[key] ?: return

        val ctx = Context(
            event,
            event.player,
            item,
            null,
            null,
            null
        )

        triggers[Trigger.DROP]?.forEach { it.perform(ctx) }
        EquippedItemsManager.runEquipTriggers(event, event.player, Trigger.DROP)
        TrinketManager.runEquipTriggers(event, event.player, Trigger.DROP)

        event.isCancelled = ctx.isCancelled

        if (ctx.removeItem) {
            val itemAfterRemoving = removeOne(item)
            if (itemAfterRemoving == null) {
                event.itemDrop.remove()
            } else {
                event.itemDrop.itemStack = itemAfterRemoving
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPlayerSwapHandItems(event: PlayerSwapHandItemsEvent) {
        event.mainHandItem.let { item ->
            val key = item.customItemKey ?: return@let
            val triggers = ItemManager.triggers[key] ?: return@let

            val ctx = Context(
                event,
                event.player,
                item,
                null,
                null,
                null
            )

            triggers[Trigger.SWAP_HAND]?.forEach { it.perform(ctx) }

            event.isCancelled = ctx.isCancelled

            if (ctx.removeItem) {
                val itemAfterRemoving = removeOne(item)
                if (ctx.isCancelled) {
                    event.player.inventory.setItemInOffHand(itemAfterRemoving)
                } else {
                    event.setMainHandItem(itemAfterRemoving)
                }
            }
        }

        event.offHandItem.let { item ->
            val key = item.customItemKey ?: return@let
            val triggers = ItemManager.triggers[key] ?: return@let

            val ctx = Context(
                event,
                event.player,
                item,
                null,
                null,
                null
            )

            triggers[Trigger.SWAP_HAND]?.forEach { it.perform(ctx) }

            event.isCancelled = ctx.isCancelled

            if (ctx.removeItem) {
                val itemAfterRemoving = removeOne(item)
                if (ctx.isCancelled) {
                    event.player.inventory.setItemInMainHand(itemAfterRemoving)
                } else {
                    event.setOffHandItem(itemAfterRemoving)
                }
            }
        }

        EquippedItemsManager.runEquipTriggers(event, event.player, Trigger.SWAP_HAND)
        TrinketManager.runEquipTriggers(event, event.player, Trigger.SWAP_HAND)
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val inventory = event.clickedInventory ?: return

        var item: ItemStack
        var triggers: Map<Trigger, List<me.weiwen.moromoro.actions.Action>>
        var trigger: Trigger

        if (event.currentItem?.customItemKey != null) {
            item = event.currentItem!!
            ItemManager.migrateItem(item)?.let {
                inventory.setItem(event.slot, it)
                return
            }
            val key = item.customItemKey

            // Equip trinket
            if (event.click == ClickType.RIGHT || event.click == ClickType.SHIFT_RIGHT) {
                val template = ItemManager.templates[key]
                if (template != null && template.slots.contains(CustomEquipmentSlot.TRINKET)) {
                    if (TrinketManager.equipTrinket(player, item)) {
                        event.currentItem = null
                    }
                    plugin.server.scheduler.scheduleSyncDelayedTask(plugin, {
                        TrinketManager.openTrinketInventory(player)
                    }, 1)

                    event.isCancelled
                }
            }

            triggers = ItemManager.triggers[key] ?: return
            if (event.cursor.isEmpty) {
                trigger = when (event.click) {
                    ClickType.RIGHT -> Trigger.RIGHT_CLICK_INVENTORY
                    ClickType.LEFT -> Trigger.LEFT_CLICK_INVENTORY
                    ClickType.MIDDLE -> Trigger.MIDDLE_CLICK_INVENTORY
                    ClickType.SHIFT_RIGHT -> Trigger.SHIFT_RIGHT_CLICK_INVENTORY
                    ClickType.SHIFT_LEFT -> Trigger.SHIFT_LEFT_CLICK_INVENTORY
                    ClickType.DOUBLE_CLICK -> Trigger.DOUBLE_CLICK_INVENTORY
                    ClickType.DROP -> Trigger.DROP_INVENTORY
                    ClickType.CONTROL_DROP -> Trigger.CONTROL_DROP_INVENTORY
                    ClickType.NUMBER_KEY -> when (event.hotbarButton) {
                        0 -> Trigger.NUMBER_1_INVENTORY
                        1 -> Trigger.NUMBER_2_INVENTORY
                        2 -> Trigger.NUMBER_3_INVENTORY
                        3 -> Trigger.NUMBER_4_INVENTORY
                        4 -> Trigger.NUMBER_5_INVENTORY
                        5 -> Trigger.NUMBER_6_INVENTORY
                        6 -> Trigger.NUMBER_7_INVENTORY
                        7 -> Trigger.NUMBER_8_INVENTORY
                        8 -> Trigger.NUMBER_9_INVENTORY
                        else -> {
                            plugin.logger.log(Level.WARNING, "Unexpected hotbar button: ${event.hotbarButton}")
                            Trigger.NUMBER_1_INVENTORY
                        }
                    }

                    ClickType.CREATIVE -> Trigger.CREATIVE_INVENTORY
                    ClickType.SWAP_OFFHAND -> Trigger.SWAP_OFFHAND_INVENTORY
                    else -> return
                }
            } else {
                trigger = when (event.click) {
                    ClickType.RIGHT -> Trigger.RIGHT_CLICK_ON_OTHER_INVENTORY
                    ClickType.LEFT -> Trigger.LEFT_CLICK_ON_OTHER_INVENTORY
                    ClickType.MIDDLE -> Trigger.MIDDLE_CLICK_ON_OTHER_INVENTORY
                    ClickType.SHIFT_RIGHT -> Trigger.SHIFT_RIGHT_CLICK_ON_OTHER_INVENTORY
                    ClickType.SHIFT_LEFT -> Trigger.SHIFT_LEFT_CLICK_ON_OTHER_INVENTORY
                    ClickType.DOUBLE_CLICK -> Trigger.DOUBLE_CLICK_ON_OTHER_INVENTORY
                    ClickType.WINDOW_BORDER_LEFT -> Trigger.LEFT_BORDER_INVENTORY
                    ClickType.WINDOW_BORDER_RIGHT -> Trigger.RIGHT_BORDER_INVENTORY
                    else -> return
                }
            }
        } else if (event.cursor.customItemKey != null) {
            item = event.cursor
            val key = item.customItemKey
            triggers = ItemManager.triggers[key] ?: return
            trigger = when (event.click) {
                ClickType.RIGHT -> Trigger.RIGHT_CLICK_ON_OTHER_INVENTORY
                ClickType.LEFT -> Trigger.LEFT_CLICK_ON_OTHER_INVENTORY
                ClickType.MIDDLE -> Trigger.MIDDLE_CLICK_ON_OTHER_INVENTORY
                ClickType.SHIFT_RIGHT -> Trigger.SHIFT_RIGHT_CLICK_ON_OTHER_INVENTORY
                ClickType.SHIFT_LEFT -> Trigger.SHIFT_LEFT_CLICK_ON_OTHER_INVENTORY
                ClickType.DOUBLE_CLICK -> Trigger.DOUBLE_CLICK_ON_OTHER_INVENTORY
                ClickType.WINDOW_BORDER_LEFT -> Trigger.LEFT_BORDER_INVENTORY
                ClickType.WINDOW_BORDER_RIGHT -> Trigger.RIGHT_BORDER_INVENTORY
                else -> return
            }
        } else {
            return
        }

        val ctx = Context(
            event,
            player,
            item,
            null,
            null,
            null
        )

        triggers[trigger]?.forEach { it.perform(ctx) }

        event.isCancelled = ctx.isCancelled

        if (ctx.removeItem) {
            inventory.setItem(event.slot, removeOne(item))
        }
    }

    /* Equipment Triggers */

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerArmorChange(event: PlayerArmorChangeEvent) {
        EquippedItemsManager.onPlayerArmorChange(event)
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerMove(event: PlayerMoveEvent) {
        EquippedItemsManager.runEquipTriggers(event, event.player, Trigger.MOVE)
        TrinketManager.runEquipTriggers(event, event.player, Trigger.MOVE)
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerJump(event: PlayerJumpEvent) {
        EquippedItemsManager.runEquipTriggers(event, event.player, Trigger.JUMP)
        TrinketManager.runEquipTriggers(event, event.player, Trigger.JUMP)
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerToggleSneak(event: PlayerToggleSneakEvent) {
        val trigger = if (event.isSneaking) Trigger.SNEAK else Trigger.UNSNEAK
        EquippedItemsManager.runEquipTriggers(event, event.player, trigger)
        TrinketManager.runEquipTriggers(event, event.player, trigger)
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerToggleSprint(event: PlayerToggleSprintEvent) {
        val trigger = if (event.isSprinting) Trigger.SPRINT else Trigger.UNSPRINT
        EquippedItemsManager.runEquipTriggers(event, event.player, trigger)
        TrinketManager.runEquipTriggers(event, event.player, trigger)
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerToggleFlight(event: PlayerToggleFlightEvent) {
        val trigger = if (event.isFlying) Trigger.FLY else Trigger.UNFLY
        EquippedItemsManager.runEquipTriggers(event, event.player, trigger)
        TrinketManager.runEquipTriggers(event, event.player, trigger)
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerToggleGlide(event: EntityToggleGlideEvent) {
        val player = event.entity as? Player ?: return
        val trigger = if (event.isGliding) Trigger.GLIDE else Trigger.UNGLIDE
        EquippedItemsManager.runEquipTriggers(event, player, trigger)
        TrinketManager.runEquipTriggers(event, player, trigger)
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerToggleSwim(event: EntityToggleSwimEvent) {
        val player = event.entity as? Player ?: return
        val trigger = if (event.isSwimming) Trigger.SWIM else Trigger.UNSWIM
        EquippedItemsManager.runEquipTriggers(event, player, trigger)
        TrinketManager.runEquipTriggers(event, player, trigger)
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerDamaged(event: EntityDamageEvent) {
        val player = event.entity as? Player ?: return
        EquippedItemsManager.runEquipTriggers(event, player, Trigger.DAMAGED)
        TrinketManager.runEquipTriggers(event, player, Trigger.DAMAGED)
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerKeyPress(event: PlayerInputEvent) {
        val player = event.player
        if (event.input.isForward && !player.currentInput.isForward) {
            EquippedItemsManager.runEquipTriggers(event, player, Trigger.KEY_DOWN_FORWARD)
            TrinketManager.runEquipTriggers(event, player, Trigger.KEY_DOWN_FORWARD)
            lastPressed.getOrPut(player.uniqueId) { mutableMapOf() }.put(InputKey.FORWARD, System.currentTimeMillis())
        }
        if (event.input.isBackward && !player.currentInput.isBackward) {
            EquippedItemsManager.runEquipTriggers(event, player, Trigger.KEY_DOWN_BACKWARD)
            TrinketManager.runEquipTriggers(event, player, Trigger.KEY_DOWN_BACKWARD)
            lastPressed.getOrPut(player.uniqueId) { mutableMapOf() }.put(InputKey.BACKWARD, System.currentTimeMillis())
        }
        if (event.input.isLeft && !player.currentInput.isLeft) {
            EquippedItemsManager.runEquipTriggers(event, player, Trigger.KEY_DOWN_LEFT)
            TrinketManager.runEquipTriggers(event, player, Trigger.KEY_DOWN_LEFT)
            lastPressed.getOrPut(player.uniqueId) { mutableMapOf() }.put(InputKey.LEFT, System.currentTimeMillis())
        }
        if (event.input.isRight && !player.currentInput.isRight) {
            EquippedItemsManager.runEquipTriggers(event, player, Trigger.KEY_DOWN_RIGHT)
            TrinketManager.runEquipTriggers(event, player, Trigger.KEY_DOWN_RIGHT)
            lastPressed.getOrPut(player.uniqueId) { mutableMapOf() }.put(InputKey.RIGHT, System.currentTimeMillis())
        }
        if (event.input.isJump && !player.currentInput.isJump) {
            EquippedItemsManager.runEquipTriggers(event, player, Trigger.KEY_DOWN_JUMP)
            TrinketManager.runEquipTriggers(event, player, Trigger.KEY_DOWN_JUMP)
            lastPressed.getOrPut(player.uniqueId) { mutableMapOf() }.put(InputKey.JUMP, System.currentTimeMillis())
        }
        if (event.input.isSneak && !player.currentInput.isSneak) {
            EquippedItemsManager.runEquipTriggers(event, player, Trigger.KEY_DOWN_SNEAK)
            TrinketManager.runEquipTriggers(event, player, Trigger.KEY_DOWN_SNEAK)
            lastPressed.getOrPut(player.uniqueId) { mutableMapOf() }.put(InputKey.SNEAK, System.currentTimeMillis())
        }
        if (event.input.isSprint && !player.currentInput.isSprint) {
            EquippedItemsManager.runEquipTriggers(event, player, Trigger.KEY_DOWN_SPRINT)
            TrinketManager.runEquipTriggers(event, player, Trigger.KEY_DOWN_SPRINT)
            lastPressed.getOrPut(player.uniqueId) { mutableMapOf() }.put(InputKey.SPRINT, System.currentTimeMillis())
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.player.equipment.let { eq ->
            eq.helmet?.let { ItemManager.migrateItem(it)?.let { eq.helmet = it } }
            eq.chestplate?.let { ItemManager.migrateItem(it)?.let { eq.chestplate = it } }
            eq.leggings?.let { ItemManager.migrateItem(it)?.let { eq.leggings = it } }
            eq.boots?.let { ItemManager.migrateItem(it)?.let { eq.boots = it } }
        }

        event.player.inventory.storageContents.forEach { item ->
            if (item != null) {
                ItemManager.migrateItem(item)?.let {
                    if (event.player.inventory.removeItem(item).isEmpty()) {
                        event.player.inventory.addItem(it)
                    }
                }
            }
        }

        TrinketManager.runEquipTriggers(event.player)
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        EquippedItemsManager.cleanUp(event.player)
        TrinketManager.cleanUp(event.player)
    }
}

private fun removeOne(item: ItemStack): ItemStack? {
    if (item.amount > 1) {
        item.amount -= 1
        return item
    } else {
        return null
    }
}

private fun removeOne(player: Player, slot: EquipmentSlot) {
    val item = player.inventory.getItem(slot)
    if (item.type != Material.AIR) {
        player.inventory.setItem(slot, removeOne(item))
    }
}