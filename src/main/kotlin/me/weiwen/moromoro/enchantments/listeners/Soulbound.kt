package me.weiwen.moromoro.enchantments.listeners

import me.weiwen.moromoro.Moromoro.Companion.plugin
import net.kyori.adventure.key.Key
import org.bukkit.ChatColor
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

object Soulbound : Listener {
    val key: Key = NamespacedKey(plugin.config.namespace, "soulbound")

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent): Boolean {
        if (event.keepInventory) {
            return false
        }

        val enchantment = Registry.ENCHANTMENT.get(key) ?: return true

        event.entity.inventory.contents.forEach {
            if (it != null && (it.containsEnchantment(enchantment) ||
                it.itemMeta?.lore?.contains(ChatColor.GRAY.toString() + "Soulbound") == true) // compat
            ) {
                event.itemsToKeep.add(it)
                event.drops.remove(it)
            }
        }

        return true
    }
}
