package me.weiwen.moromoro.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Material

class MaterialSerializer : KSerializer<Material> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Material", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Material {
        val string = decoder.decodeString()
        return Material.valueOf(string.uppercase())
    }

    override fun serialize(encoder: Encoder, value: Material) {
        val string = value.toString()
        return encoder.encodeString(string)
    }
}
