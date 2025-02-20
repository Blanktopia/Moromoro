package me.weiwen.moromoro.managers

import me.weiwen.moromoro.Moromoro
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.persistence.PersistentDataType

object SoulboundListener : Listener {
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        if (event.keepInventory) {
            return
        }

        event.entity.inventory.contents.forEach {
            if (it != null && (it.type == Material.ENDER_CHEST || it.itemMeta.persistentDataContainer.get(
                    NamespacedKey(
                        Moromoro.plugin.config.namespace,
                        "soulbound"
                    ), PersistentDataType.BYTE
                ) == 1.toByte())
            ) {
                event.itemsToKeep.add(it)
                event.drops.remove(it)
            }
        }

        return
    }
}

