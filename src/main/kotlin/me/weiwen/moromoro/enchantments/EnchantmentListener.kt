package me.weiwen.moromoro.items

import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

private fun removeOne(item: ItemStack): ItemStack? {
    if (item.amount > 1) {
        item.amount -= 1
        return item
    } else {
        return null
    }
}

private fun removeOne(player: Player, slot: EquipmentSlot) {
    player.inventory.setItem(slot, player.inventory.getItem(slot)?.let { removeOne(it) })
}