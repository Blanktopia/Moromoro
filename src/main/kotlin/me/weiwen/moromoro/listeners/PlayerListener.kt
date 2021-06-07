package me.weiwen.moromoro.listeners

import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.actions.Trigger
import me.weiwen.moromoro.extensions.customItemKey
import me.weiwen.moromoro.extensions.isReallyInteractable
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import java.util.logging.Level

class PlayerListener(val plugin: Moromoro) : Listener {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val item = when (event.hand) {
            EquipmentSlot.HAND -> event.player.inventory.itemInMainHand
            EquipmentSlot.OFF_HAND -> event.player.inventory.itemInOffHand
            else -> return
        }

        val key = item.customItemKey ?: return

        // Cancel if interacting with a block
        if (event.action == Action.RIGHT_CLICK_BLOCK && !event.player.isSneaking) {
            val blockType = event.clickedBlock?.type
            if (blockType?.isReallyInteractable == true) {
                event.setUseItemInHand(Event.Result.DENY)
                return
            }
        }

        // Prevent double interaction
        if (event.useInteractedBlock() == Event.Result.DENY) {
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

        val triggers = plugin.itemManager.triggers[key] ?: return
        when (event.action) {
            Action.LEFT_CLICK_BLOCK -> {
                triggers[Trigger.LEFT_CLICK_BLOCK]?.forEach { it.perform(ctx) }
                triggers[Trigger.LEFT_CLICK]?.forEach { it.perform(ctx) }
            }
            Action.RIGHT_CLICK_BLOCK -> {
                triggers[Trigger.RIGHT_CLICK_BLOCK]?.forEach { it.perform(ctx) }
                triggers[Trigger.RIGHT_CLICK]?.forEach { it.perform(ctx) }
            }
            Action.LEFT_CLICK_AIR -> {
                triggers[Trigger.LEFT_CLICK_AIR]?.forEach { it.perform(ctx) }
                triggers[Trigger.LEFT_CLICK]?.forEach { it.perform(ctx) }
            }
            Action.RIGHT_CLICK_AIR -> {
                triggers[Trigger.RIGHT_CLICK_AIR]?.forEach { it.perform(ctx) }
                triggers[Trigger.RIGHT_CLICK]?.forEach { it.perform(ctx) }
            }
            Action.PHYSICAL -> return
        }

        if (ctx.isCancelled) {
            event.setUseItemInHand(Event.Result.DENY)
        }

        if (ctx.removeItem) {
            event.hand?.let { removeOne(event.player, it) }
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
        val triggers = plugin.itemManager.triggers[key] ?: return

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
        val triggers = plugin.itemManager.triggers[key] ?: return

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
        val triggers = plugin.itemManager.triggers[key] ?: return

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
        val triggers = plugin.itemManager.triggers[key] ?: return

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
    fun onPlayerItemConsume(event: PlayerItemConsumeEvent) {
        val item = event.item

        val key = item.customItemKey ?: return
        val triggers = plugin.itemManager.triggers[key] ?: return

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
        val triggers = plugin.itemManager.triggers[key] ?: return

        val ctx = Context(
            event,
            event.player,
            item,
            null,
            null,
            null
        )

        triggers[Trigger.DROP]?.forEach { it.perform(ctx) }

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
    fun onInventoryClick(event: InventoryClickEvent) {
        val inventory = event.clickedInventory ?: return
        val item = inventory.getItem(event.slot) ?: return

        val key = item.customItemKey ?: return
        val triggers = plugin.itemManager.triggers[key] ?: return

        val player = event.whoClicked as? Player ?: return

        val trigger = when (event.click) {
            ClickType.RIGHT -> Trigger.RIGHT_CLICK_INVENTORY
            ClickType.LEFT -> Trigger.LEFT_CLICK_INVENTORY
            ClickType.MIDDLE -> Trigger.MIDDLE_CLICK_INVENTORY
            ClickType.SHIFT_RIGHT -> Trigger.SHIFT_RIGHT_CLICK_INVENTORY
            ClickType.SHIFT_LEFT -> Trigger.SHIFT_LEFT_CLICK_INVENTORY
            ClickType.DOUBLE_CLICK -> Trigger.DOUBLE_CLICK_INVENTORY
            ClickType.DROP -> Trigger.DROP_INVENTORY
            ClickType.CONTROL_DROP -> Trigger.CONTROL_DROP_INVENTORY
            ClickType.WINDOW_BORDER_LEFT -> Trigger.LEFT_BORDER_INVENTORY
            ClickType.WINDOW_BORDER_RIGHT -> Trigger.RIGHT_BORDER_INVENTORY
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
            ClickType.UNKNOWN -> return
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
    player.inventory.setItem(slot, player.inventory.getItem(slot)?.let { removeOne(it) })
}