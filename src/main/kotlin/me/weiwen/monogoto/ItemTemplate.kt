package me.weiwen.monogoto

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ItemTemplate(val material: Material) {
    fun build(amount: Int = 1): ItemStack {
        return ItemStack(material, amount)
    }
}

