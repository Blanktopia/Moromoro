package me.weiwen.moromoro.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.attribute.Attribute

class AttributeSerializer : KSerializer<Attribute> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Attribute", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Attribute {
        val string = decoder.decodeString()
        val key = NamespacedKey.fromString(string) ?: return Attribute.MAX_HEALTH
        return Registry.ATTRIBUTE.get(key) ?: Attribute.MAX_HEALTH
    }

    override fun serialize(encoder: Encoder, value: Attribute) {
        return encoder.encodeString(value.key.toString())
    }
}
