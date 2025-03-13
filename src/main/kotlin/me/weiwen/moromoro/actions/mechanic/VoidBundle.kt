package me.weiwen.moromoro.actions.mechanic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.extensions.isSoulbound
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.meta.BundleMeta

@Serializable
@SerialName("void-bundle")
object VoidBundle : Action {
    override fun perform(ctx: Context): Boolean {
        val event = ctx.event as? InventoryClickEvent ?: return false
        val item = ctx.item ?: return false
        val other = if (event.cursor == item) {
            event.currentItem
        } else {
            event.cursor
        }

        if (other?.isSoulbound == true) {
            return false
        }

        if (other == null || other.isEmpty) {
            return false
        }

        val itemMeta = item.itemMeta as? BundleMeta ?: return false
        itemMeta.setItems(listOf())
        item.itemMeta = itemMeta
        return true
    }
}

