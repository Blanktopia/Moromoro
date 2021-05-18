package me.weiwen.moromoro.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentWrapper

class EnchantmentSerializer : KSerializer<Enchantment> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Enchantment", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Enchantment {
        val string = decoder.decodeString()
        return EnchantmentWrapper(string)
    }

    override fun serialize(encoder: Encoder, value: Enchantment) {
        val key = value.key.toString()
        return encoder.encodeString(key)
    }
}
