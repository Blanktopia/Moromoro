package me.weiwen.moromoro.actions.item

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

@Serializable
@SerialName("equip-item")
data class EquipItem(val slot: EquipmentSlot) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        val item = ctx.item ?: return false
        val equippedItem: ItemStack? = player.inventory.getItem(slot)

        if (equippedItem == item) {
            player.inventory.setItem(slot, null)
            player.inventory.addItem(item)
            return true
        } else if (equippedItem == null) {
            val couldntRemove = player.inventory.removeItem(item)
            if (couldntRemove.isEmpty()) {
                player.inventory.setItem(slot, item)
                return true
            }
        }

        return false
    }
}

