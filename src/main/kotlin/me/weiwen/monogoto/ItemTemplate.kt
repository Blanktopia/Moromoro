package me.weiwen.monogoto

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

data class ItemTemplate(
    val material: Material,
    val name: String?,
    val lore: String?,
    val enchantments: Map<Enchantment, Int>,
    val unbreakable: Boolean,
    val customModelData: Int?
) {
    fun build(amount: Int = 1): ItemStack {
        return ItemStack(material, amount).apply {
            val itemMeta = this.itemMeta as ItemMeta

            name?.let { itemMeta.setDisplayName(it) }
            lore?.let { itemMeta.lore = it.lines() }
            enchantments.forEach { (enchant, level) -> itemMeta.addEnchant(enchant, level, true) }
            itemMeta.isUnbreakable = unbreakable
            customModelData?.let { itemMeta.setCustomModelData(it) }

            this.itemMeta = itemMeta
        }
    }
}

