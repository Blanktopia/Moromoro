package me.weiwen.moromoro.enchantments.listeners

import me.weiwen.moromoro.Moromoro.Companion.plugin
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareAnvilEvent

object Final : Listener {
    val key = NamespacedKey(plugin.config.namespace, "final")

    @EventHandler
    fun onPrepareAnvilEvent(event: PrepareAnvilEvent) {
        val items = event.inventory.contents ?: return
        val enchantment = Registry.ENCHANTMENT.get(key) ?: return
        if (items.any { it != null && it.containsEnchantment    (enchantment) }) {
            event.result = null
            for (viewer in event.viewers) {
                (viewer as? Player)?.updateInventory()
            }
        }
    }
}
