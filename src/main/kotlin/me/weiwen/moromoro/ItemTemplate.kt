package me.weiwen.moromoro

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

data class ItemTemplate(
    val key: String,
    val material: Material,
    val name: String?,
    val lore: String?,
    val enchantments: Map<Enchantment, Int>,
    val unbreakable: Boolean,
    val customModelData: Int?,
) {
    fun build(amount: Int = 1): ItemStack {
        return ItemStack(material, amount).also {
            val itemMeta = it.itemMeta as ItemMeta

            name?.let { itemMeta.setDisplayName(it) }
            lore?.let { itemMeta.lore = it.lines() }
            enchantments.forEach { (enchant, level) -> itemMeta.addEnchant(enchant, level, true) }
            itemMeta.isUnbreakable = unbreakable
            customModelData?.let { data -> itemMeta.setCustomModelData(data) }

            val data = itemMeta.persistentDataContainer
            data.set(NamespacedKey(Moromoro.plugin, "key"), PersistentDataType.STRING, key)

            it.itemMeta = itemMeta
        }
    }
}

