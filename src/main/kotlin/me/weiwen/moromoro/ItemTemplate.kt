@file:UseSerializers(MaterialSerializer::class, EnchantmentSerializer::class, UUIDSerializer::class)

package me.weiwen.moromoro

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Trigger
import me.weiwen.moromoro.extensions.setHeadUrl
import me.weiwen.moromoro.managers.ItemManager
import me.weiwen.moromoro.serializers.EnchantmentSerializer
import me.weiwen.moromoro.serializers.FormattedString
import me.weiwen.moromoro.serializers.MaterialSerializer
import me.weiwen.moromoro.serializers.UUIDSerializer
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import java.util.*

@Serializable
data class ItemProperties(
    val material: Material,
    val name: FormattedString? = null,
    val lore: FormattedString? = null,
    @SerialName("custom-model-data")
    val customModelData: Int? = null,
    val head: String? = null,
    val unbreakable: Boolean = false,
    val enchantments: Map<Enchantment, Int> = mapOf(),
    val attributes: List<AttributeModifier> = listOf(),
    val flags: List<ItemFlag> = listOf(),
    val triggers: Map<Trigger, List<Action>> = mapOf()
)

@Serializable
data class AttributeModifier(
    val attribute: Attribute,
    val uuid: UUID,
    val name: String,
    val amount: Double,
    val operation: org.bukkit.attribute.AttributeModifier.Operation,
    val slot: EquipmentSlot,
)

val AttributeModifier.modifier: org.bukkit.attribute.AttributeModifier
    get() = org.bukkit.attribute.AttributeModifier(uuid, name, amount, operation, slot)

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

            properties.attributes.forEach { itemMeta.addAttributeModifier(it.attribute, it.modifier) }

            properties.flags.forEach { itemMeta.addItemFlags(it) }

            val data = itemMeta.persistentDataContainer
            data.set(NamespacedKey(Moromoro.plugin.config.namespace, "type"), PersistentDataType.STRING, key)

            item.itemMeta = itemMeta

            properties.head?.let { item.setHeadUrl(properties.name?.value ?: "", it) }
        }
    }

    fun registerTriggers(itemManager: ItemManager) {
        itemManager.registerTriggers(key, properties.triggers)
    }
}

