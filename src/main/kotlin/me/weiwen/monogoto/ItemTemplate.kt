package me.weiwen.monogoto

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

data class ItemTemplate(val material: Material, val name: String?) {
    fun build(amount: Int = 1): ItemStack {
        return ItemStack(material, amount).apply {
            val itemMeta = this.itemMeta as ItemMeta

            name?.let { itemMeta.setDisplayName(name) }

            this.itemMeta = itemMeta
        }
    }
}

