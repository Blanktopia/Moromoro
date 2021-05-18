package me.weiwen.moromoro.extensions

import me.weiwen.moromoro.Moromoro
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

val ItemStack.customItemKey: String? get() {
    val data = itemMeta?.persistentDataContainer ?: return null
    return data.get(NamespacedKey(Moromoro.plugin.config.namespace, "key"), PersistentDataType.STRING)
}
