package me.weiwen.moromoro.managers

import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.extensions.isUnenchantable
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.GrindstoneInventory
import org.bukkit.persistence.PersistentDataType

object UnenchantableListener : Listener {
    @EventHandler
    private fun onEnchantItem(event: EnchantItemEvent) {
        val item = event.item
        if (item.itemMeta.persistentDataContainer.get(NamespacedKey(Moromoro.plugin.config.namespace, "unenchantable"), PersistentDataType.BYTE) == 1.toByte()) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private fun onPrepareAnvil(event: PrepareAnvilEvent) {
        val inventory = event.inventory

        val target = inventory.firstItem ?: return
        val sacrifice = inventory.secondItem ?: return

        if (target.isUnenchantable || sacrifice.isUnenchantable) {
            event.result = null
            (event.viewers.get(0) as? Player)?.updateInventory()
            return
        }
    }

    @EventHandler
    private fun onInventoryDrag(event: InventoryDragEvent) {
        val inventory = event.inventory as? GrindstoneInventory ?: return
        val player = event.whoClicked
        if (player !is Player) return

        val target = inventory.getItem(0) ?: return
        val sacrifice = inventory.getItem(1) ?: return

        if (target.isUnenchantable || sacrifice.isUnenchantable) {
            inventory.setItem(2, null)
        }
    }
}

