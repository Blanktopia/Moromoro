package me.weiwen.moromoro.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.key.Key
import org.bukkit.Registry
import org.bukkit.inventory.ItemType

class ItemTypeSerializer : KSerializer<ItemType> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Material", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ItemType {
        val string = decoder.decodeString()
        val key = Key.key(string)
        return Registry.ITEM.get(key) ?: ItemType.STICK
    }

    override fun serialize(encoder: Encoder, value: ItemType) {
        val key = Registry.ITEM.getKey(value)
        val string = key?.asString() ?: ""
        return encoder.encodeString(string)
    }
}
