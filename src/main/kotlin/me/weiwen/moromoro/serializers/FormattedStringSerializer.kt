package me.weiwen.moromoro.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

@Serializable(with = FormattedStringSerializer::class)
data class FormattedString(val value: String)

val FormattedString.component: Component
    get() = MiniMessage.miniMessage().deserialize(value)

val FormattedString.text: String
    get() = MiniMessage.miniMessage().stripTags(value)

object FormattedStringSerializer : KSerializer<FormattedString> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("FormattedString", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): FormattedString {
        val string = decoder.decodeString()
        return FormattedString(string)
    }

    override fun serialize(encoder: Encoder, value: FormattedString) {
        return encoder.encodeString(value.value)
    }
}
