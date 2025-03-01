@file:UseSerializers(MaterialSerializer::class)

package me.weiwen.moromoro.actions.customBlock

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.blocks.EntityCustomBlock
import me.weiwen.moromoro.items.ItemManager
import me.weiwen.moromoro.items.item
import me.weiwen.moromoro.serializers.MaterialSerializer
import org.bukkit.NamespacedKey
import org.bukkit.entity.ItemDisplay
import org.bukkit.persistence.PersistentDataType

@Serializable
@SerialName("toggle-custom-block")
data class ToggleCustomBlock(val key: String) : Action {
    override fun perform(ctx: Context): Boolean {
        val customBlock = ctx.customBlock as? EntityCustomBlock ?: return false
        val entity = customBlock.entity as? ItemDisplay ?: return false
        val template = ItemManager.templates.get(key) ?: return false
        entity.setItemStack(template.item(key, 1))
        entity.apply {
            persistentDataContainer.set(
                NamespacedKey(Moromoro.plugin.config.namespace, "type"),
                PersistentDataType.STRING,
                key
            )
        }
        return true
    }
}
