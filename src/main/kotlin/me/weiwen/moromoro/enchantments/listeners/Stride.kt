package me.weiwen.moromoro.enchantments.listeners

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import me.weiwen.moromoro.Moromoro.Companion.plugin
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object Stride : Listener {
    val key = NamespacedKey(plugin.config.namespace, "stride")

    @EventHandler
    fun onPlayerArmorChange(event: PlayerArmorChangeEvent) {
        val newItem = event.newItem
        val oldItem = event.oldItem
        val enchantment = Registry.ENCHANTMENT.get(key) ?: return
        if (newItem != null && newItem.containsEnchantment(enchantment)) {
            event.player.walkSpeed = when (newItem.getEnchantmentLevel(enchantment)) {
                0 -> 0.2f
                1 -> 0.225f
                2 -> 0.240f
                3 -> 0.260f
                4 -> 0.270f
                5 -> 0.275f
                else -> 0.2f
            }
        } else if (oldItem != null && oldItem.containsEnchantment(enchantment)) {
            event.player.walkSpeed = 0.2f
        }
    }
}
