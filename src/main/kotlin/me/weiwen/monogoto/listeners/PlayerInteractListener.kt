package me.weiwen.monogoto.listeners

import me.weiwen.monogoto.Monogoto
import me.weiwen.monogoto.actions.BreakBlockAction
import me.weiwen.monogoto.actions.Context
import me.weiwen.monogoto.actions.Trigger
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.persistence.PersistentDataType
import java.util.logging.Level

class PlayerInteractListener(val plugin: Monogoto) : Listener {
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val item = when (event.hand) {
            EquipmentSlot.HAND -> event.player.inventory.itemInMainHand
            EquipmentSlot.OFF_HAND -> event.player.inventory.itemInOffHand
            else -> return
        }

        val data = item.itemMeta?.persistentDataContainer ?: return
        val key = data.get(NamespacedKey(plugin, "key"), PersistentDataType.STRING) ?: return
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
    fun onBreakBlock(event: BlockBreakEvent) {
        val item = event.player.inventory.itemInMainHand

        val data = item.itemMeta?.persistentDataContainer ?: return
        val key = data.get(NamespacedKey(plugin, "key"), PersistentDataType.STRING) ?: return
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
}
