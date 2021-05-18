package me.weiwen.moromoro.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Material
import org.bukkit.block.Biome

class BiomeSerializer : KSerializer<Biome> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Biome", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Biome {
        val string = decoder.decodeString()
        return Biome.valueOf(string)
    }

    override fun serialize(encoder: Encoder, value: Biome) {
        val string = value.toString()
        return encoder.encodeString(string)
    }
}
