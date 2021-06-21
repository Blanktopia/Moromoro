package me.weiwen.moromoro.extensions

import me.weiwen.moromoro.Moromoro
import org.bukkit.NamespacedKey
import org.bukkit.entity.Entity
import org.bukkit.persistence.PersistentDataType

val Entity.customItemKey: String?
    get() {
        val data = persistentDataContainer
        return data.get(NamespacedKey(Moromoro.plugin.config.namespace, "type"), PersistentDataType.STRING)
    }
