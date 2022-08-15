package me.weiwen.moromoro.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.hooks.EssentialsHook
import org.bukkit.inventory.RecipeChoice
import java.util.logging.Level

class RecipeChoiceSerializer : KSerializer<RecipeChoice> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("RecipeChoice", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): RecipeChoice {
        val string = decoder.decodeString()

        val items = string.split(',')
            .mapNotNull {
                val item = EssentialsHook.getItemStack(it)

                if (item == null) {
                    Moromoro.plugin.logger.log(Level.WARNING, "Unnknown item: ${it}")
                }

                item
            }

        return RecipeChoice.ExactChoice(items)
    }

    override fun serialize(encoder: Encoder, value: RecipeChoice) {
        val string = when (value) {
            is RecipeChoice.ExactChoice -> value.choices.mapNotNull { EssentialsHook.getName(it) }
            is RecipeChoice.MaterialChoice -> value.choices.map { it.toString() }
            else -> throw RuntimeException("Unknown RecipeChoice subclass")
        }.joinToString(",")

        return encoder.encodeString(string)
    }
}
