package me.weiwen.moromoro.listeners

import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.actions.Trigger
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.persistence.PersistentDataType

class PlayerInteractListener(val plugin: Moromoro) : Listener {
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val item = when (event.hand) {
            EquipmentSlot.HAND -> event.player.inventory.itemInMainHand
            EquipmentSlot.OFF_HAND -> event.player.inventory.itemInOffHand
            else -> return
        }

        val data = item.itemMeta?.persistentDataContainer ?: return
        val key = data.get(NamespacedKey(plugin.config.namespace, "key"), PersistentDataType.STRING) ?: return
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
            Action.LEFT_CLICK_BLOCK -> triggers[Trigger.LEFT_CLICK_BLOCK]?.forEach { it.perform(ctx) }
            Action.RIGHT_CLICK_BLOCK -> triggers[Trigger.RIGHT_CLICK_BLOCK]?.forEach { it.perform(ctx) }
            Action.LEFT_CLICK_AIR -> return
            Action.RIGHT_CLICK_AIR -> return
            Action.PHYSICAL -> return
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        val item = event.player.inventory.itemInMainHand

        val data = item.itemMeta?.persistentDataContainer ?: return
        val key = data.get(NamespacedKey(plugin.config.namespace, "key"), PersistentDataType.STRING) ?: return
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
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent) {
        val item = event.player.inventory.itemInMainHand
        event.blockPlaced

        val data = item.itemMeta?.persistentDataContainer ?: return
        val key = data.get(NamespacedKey(plugin.config.namespace, "key"), PersistentDataType.STRING) ?: return
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
    }
}
