package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.managers.AttributeModifier
import me.weiwen.moromoro.managers.modifier

@Serializable
@SerialName("add-attribute-modifier")
data class AddAttributeModifier(
    @SerialName("attribute-modifiers") val attributeModifiers: List<AttributeModifier>
) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false

        attributeModifiers.forEach {
            try {
                player.getAttribute(it.attribute)?.addModifier(it.modifier)
            } catch (e: Exception) {
                Moromoro.plugin.logger.warning("$e")
            }
        }

        return true
    }
}
