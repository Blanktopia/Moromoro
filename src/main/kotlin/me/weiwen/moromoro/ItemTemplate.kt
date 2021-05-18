@file:UseSerializers(MaterialSerializer::class, EnchantmentSerializer::class)

package me.weiwen.moromoro

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Trigger
import me.weiwen.moromoro.serializers.EnchantmentSerializer
import me.weiwen.moromoro.serializers.FormattedString
import me.weiwen.moromoro.serializers.MaterialSerializer
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

@Serializable
data class ItemProperties(
    val material: Material,
    val name: FormattedString? = null,
    val lore: FormattedString? = null,
    val customModelData: Int? = null,
    val unbreakable: Boolean = false,
    val enchantments: Map<Enchantment, Int> = mapOf(),
    val triggers: Map<Trigger, List<Action>> = mapOf()
)

data class ItemTemplate(
    val key: String,
    val properties: ItemProperties
) {
    fun build(amount: Int = 1): ItemStack {
        return ItemStack(properties.material, amount).also { item ->
            val itemMeta = item.itemMeta as ItemMeta

            properties.name?.let { itemMeta.setDisplayName(it.value) }
            properties.lore?.let { itemMeta.lore = it.value.lines() }
            itemMeta.isUnbreakable = properties.unbreakable
            properties.customModelData?.let { data -> itemMeta.setCustomModelData(data) }

            properties.enchantments.forEach { (enchant, level) -> itemMeta.addEnchant(enchant, level, true) }

            val data = itemMeta.persistentDataContainer
            data.set(NamespacedKey(Moromoro.plugin.config.namespace, "key"), PersistentDataType.STRING, key)

            item.itemMeta = itemMeta
        }
    }

    fun registerTriggers(itemManager: ItemManager) {
        itemManager.registerTriggers(key, properties.triggers)
    }
}

