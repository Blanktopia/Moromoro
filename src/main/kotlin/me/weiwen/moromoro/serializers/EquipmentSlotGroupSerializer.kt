package me.weiwen.moromoro.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.inventory.EquipmentSlotGroup

class EquipmentSlotGroupSerializer : KSerializer<EquipmentSlotGroup> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("EquipmentSlotGroup", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): EquipmentSlotGroup {
        val string = decoder.decodeString()
        return EquipmentSlotGroup.getByName(string) ?: EquipmentSlotGroup.ANY
    }

    override fun serialize(encoder: Encoder, value: EquipmentSlotGroup) {
        val key = value.toString()
        return encoder.encodeString(key)
    }
}
