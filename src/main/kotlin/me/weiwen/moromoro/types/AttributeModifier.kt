package me.weiwen.moromoro.types

import kotlinx.serialization.Serializable
import me.weiwen.moromoro.Moromoro.Companion.plugin
import me.weiwen.moromoro.serializers.AttributeSerializer
import me.weiwen.moromoro.serializers.EquipmentSlotGroupSerializer
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.inventory.EquipmentSlotGroup

@Serializable
data class AttributeModifier(
    @Serializable(with = AttributeSerializer::class)
    val attribute: Attribute,
    val name: String,
    val amount: Double,
    val operation: org.bukkit.attribute.AttributeModifier.Operation,
    @Serializable(with = EquipmentSlotGroupSerializer::class)
    val slot: EquipmentSlotGroup = EquipmentSlotGroup.ANY,
)

val AttributeModifier.modifier: org.bukkit.attribute.AttributeModifier
    get() = org.bukkit.attribute.AttributeModifier(key, amount, operation, slot)

val AttributeModifier.key: NamespacedKey
    get() = NamespacedKey(plugin.config.namespace, name)