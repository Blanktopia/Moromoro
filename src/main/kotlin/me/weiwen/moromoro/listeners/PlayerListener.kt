package me.weiwen.moromoro.listeners

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import com.destroystokyo.paper.event.player.PlayerJumpEvent
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.actions.Trigger
import me.weiwen.moromoro.extensions.customItemKey
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityToggleGlideEvent
import org.bukkit.event.entity.EntityToggleSwimEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.logging.Level

class PlayerListener(val plugin: Moromoro) : Listener {
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerInteract(event: PlayerInteractEvent) {
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
            null,
            event.clickedBlock,
            event.blockFace
        )

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

        event.isCancelled = ctx.isCancelled

        if (ctx.removeItem) {
            event.hand?.let { removeOne(event.player, it) }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
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

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
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

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
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

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
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

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
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

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
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