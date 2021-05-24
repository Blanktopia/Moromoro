package me.weiwen.moromoro.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Color
import org.bukkit.Color.*
import org.bukkit.DyeColor

class ColorSerializer : KSerializer<Color> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Color", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Color {
        val string = decoder.decodeString()

        return try {
            DyeColor.valueOf(string).color
        } catch (e: IllegalArgumentException) {
            fromRGB(string.toInt(16))
        }
    }

    override fun serialize(encoder: Encoder, value: Color) {
        val string = value.toString()

        val hex = value.asRGB().toString(16)

        return encoder.encodeString(string)
    }
}
