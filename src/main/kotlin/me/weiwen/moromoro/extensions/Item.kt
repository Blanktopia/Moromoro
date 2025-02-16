package me.weiwen.moromoro.extensions

import me.weiwen.moromoro.Moromoro
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import java.net.URI

var ItemStack.customItemKey: String?
    get() {
        val data = itemMeta?.persistentDataContainer ?: return null
        return data.get(NamespacedKey(Moromoro.plugin.config.namespace, "type"), PersistentDataType.STRING)
    }
    set(key) {
        val itemMeta = itemMeta
        val data = itemMeta?.persistentDataContainer ?: return
        if (key != null) {
            data.set(NamespacedKey(Moromoro.plugin.config.namespace, "type"), PersistentDataType.STRING, key)
        }
        this.itemMeta = itemMeta
    }

fun ItemStack.setHeadHash(name: String, hash: String) {
    val url = "http://textures.minecraft.net/texture/$hash"
    setHeadUrl(name, url)
}

fun ItemStack.setHeadUrl(name: String, url: String) {
    var name = Regex("[^A-z0-9]").replace(name, "")
    if (name.length > 16) {
        name = name.substring(0, 16)
    }
    val profile = Bukkit.createProfile(null, name)
    val textures = profile.textures
    textures.skin = URI.create(url).toURL()
    profile.setTextures(textures)

    val skullMeta = itemMeta as? SkullMeta ?: return
    skullMeta.playerProfile = profile
    itemMeta = skullMeta
}

var ItemStack.isUnenchantable: Boolean
    get() {
        return itemMeta.persistentDataContainer.get(NamespacedKey(Moromoro.plugin.config.namespace, "unenchantable"), PersistentDataType.BYTE) == 1.toByte()
    }
    set(unenchantable) {
        val meta = itemMeta
        if (unenchantable) {
            meta.persistentDataContainer.set(NamespacedKey(Moromoro.plugin.config.namespace, "unenchantable"), PersistentDataType.BYTE, 1.toByte())
        } else {
            meta.persistentDataContainer.remove(NamespacedKey(Moromoro.plugin.config.namespace, "unenchantable"))
        }
        itemMeta = meta
    }

var ItemStack.isSoulbound: Boolean
    get() {
        return itemMeta.persistentDataContainer.get(NamespacedKey(Moromoro.plugin.config.namespace, "soulbound"), PersistentDataType.BYTE) == 1.toByte()
    }
    set(soulbound) {
        val meta = itemMeta
        if (soulbound) {
            meta.persistentDataContainer.set(NamespacedKey(Moromoro.plugin.config.namespace, "soulbound"), PersistentDataType.BYTE, 1.toByte())
            val lore = meta.lore() ?: mutableListOf()
            lore.removeAll {
                PlainTextComponentSerializer.plainText().serialize(it) == "Soulbound"
            }
            lore.add(0, Component.text("Soulbound")
                .color(TextColor.color(0xAAAAAA))
                .decoration(TextDecoration.ITALIC, false))
            meta.lore(lore)
        } else {
           meta.persistentDataContainer.remove(NamespacedKey(Moromoro.plugin.config.namespace, "soulbound"))
            meta.lore()?.let { lore ->
                lore.removeAll {
                    PlainTextComponentSerializer.plainText().serialize(it) == "Soulbound"
                }
                meta.lore(lore)
            }
        }
        itemMeta = meta
    }
