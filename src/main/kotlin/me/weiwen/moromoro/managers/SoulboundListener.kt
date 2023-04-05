package me.weiwen.moromoro.managers

import me.weiwen.moromoro.Moromoro
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

object SoulboundListener : Listener {
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        if (event.keepInventory) {
            return
        }

        val items = event.entity.inventory.contents?.map {
            if (it != null && (it.itemMeta.persistentDataContainer.get(NamespacedKey(Moromoro.plugin.config.namespace, "soulbound"), PersistentDataType.BYTE) == 1.toByte()) ) {
                event.drops.remove(it)
                it
            } else {
                ItemStack(Material.AIR)
            }
        } ?: return

        event.keepInventory = true
        event.entity.inventory.contents = items.toTypedArray()
        return
    }
}

