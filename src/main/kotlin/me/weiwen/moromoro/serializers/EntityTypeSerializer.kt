package me.weiwen.moromoro.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.entity.EntityType

class EntityTypeSerializer : KSerializer<EntityType> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("EntityType", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): EntityType {
        val string = decoder.decodeString()
        return EntityType.valueOf(string)
    }

    override fun serialize(encoder: Encoder, value: EntityType) {
        val string = value.toString()
        return encoder.encodeString(string)
    }
}
