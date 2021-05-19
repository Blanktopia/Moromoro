package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

@Serializable
@SerialName("equip-item")
data class EquipItem(val slot: EquipmentSlot) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player
        val item = ctx.item
        val equippedItem: ItemStack? = ctx.player.inventory.getItem(slot)

        if (equippedItem == item) {
            player.inventory.setItem(slot, null)
            player.inventory.addItem(item)
        } else if (equippedItem == null) {
            player.inventory.removeItem(item)
            player.inventory.setItem(slot, item)
        }

        return true
    }
}

