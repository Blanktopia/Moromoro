package me.weiwen.moromoro.types

import kotlinx.serialization.Serializable
import me.weiwen.moromoro.serializers.AttributeSerializer
import me.weiwen.moromoro.serializers.UUIDSerializer
import org.bukkit.attribute.Attribute
import org.bukkit.inventory.EquipmentSlot
import java.util.*

@Serializable
data class AttributeModifier(
    @Serializable(with = AttributeSerializer::class)
    val attribute: Attribute,
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID,
    val name: String,
    val amount: Double,
    val operation: org.bukkit.attribute.AttributeModifier.Operation,
    val slot: EquipmentSlot? = null,
)
val AttributeModifier.modifier: org.bukkit.attribute.AttributeModifier
    get() = org.bukkit.attribute.AttributeModifier(uuid, name, amount, operation, slot)
