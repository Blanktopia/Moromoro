package me.weiwen.moromoro.enchantments.listeners

import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.Moromoro.Companion.plugin
import net.kyori.adventure.key.Key
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.persistence.PersistentDataType

object Soulbound : Listener {
    val key: Key = NamespacedKey(plugin.config.namespace, "soulbound")

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent): Boolean {
        if (event.keepInventory) {
            return false
        }

        val enchantment = Registry.ENCHANTMENT.get(key) ?: return true

        for (item in event.entity.inventory.contents) {
            if (item == null) continue
            if (item.containsEnchantment(enchantment) ||
                item.itemMeta?.lore?.contains(ChatColor.GRAY.toString() + "Soulbound") == true || // compat
                item.type == Material.ENDER_CHEST ||
                item.itemMeta.persistentDataContainer.get(
                    NamespacedKey(Moromoro.plugin.config.namespace, "soulbound"),
                    PersistentDataType.BYTE
                ) == 1.toByte()
            ) {
                event.itemsToKeep.add(item)
                event.drops.remove(item)
            }
        }

        return true
    }
}
