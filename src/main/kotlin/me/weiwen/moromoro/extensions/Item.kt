package me.weiwen.moromoro.extensions

import me.weiwen.moromoro.Moromoro
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

val ItemStack.customItemKey: String?
    get() {
        val data = itemMeta?.persistentDataContainer ?: return null
        return data.get(NamespacedKey(Moromoro.plugin.config.namespace, "type"), PersistentDataType.STRING)
    }

fun ItemStack.setHeadHash(name: String, hash: String) {
    val url = "http://textures.minecraft.net/texture/$hash"
    setHeadUrl(name, url)
}

fun ItemStack.setHeadUrl(name: String, url: String) {
    val bytes = Base64.getEncoder().encode("{textures:{SKIN:{url:\"$url\"}}}".toByteArray())
    setHeadBase64(name, String(bytes))
}

fun ItemStack.setHeadBase64(name: String, base64: String) {
    val uuid = base64.hashCode()
    Bukkit.getUnsafe().modifyItemStack(
        this,
        "{SkullOwner:{Name:\"$name\",Id:[I;-1,$uuid,-1,$uuid],Properties:{textures:[{Value:\"$base64\"}]}}}"
    )
}
