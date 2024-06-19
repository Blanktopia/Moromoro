package me.weiwen.moromoro.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment

class EnchantmentSerializer : KSerializer<Enchantment> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Enchantment", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Enchantment {
        val string = decoder.decodeString()
        if (string.contains(':')) {
            val (namespace, key) = string.split(':', limit = 2)
            return Enchantment.getByKey(NamespacedKey(namespace, key)) ?: Enchantment.PROTECTION
        }
        return Enchantment.getByName(string) ?: Enchantment.PROTECTION
    }

    override fun serialize(encoder: Encoder, value: Enchantment) {
        val key = value.key.toString()
        return encoder.encodeString(key)
    }
}
