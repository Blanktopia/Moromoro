package me.weiwen.moromoro.actions.player

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.types.AttributeModifier
import me.weiwen.moromoro.types.key

@Serializable
@SerialName("remove-attribute-modifier")
data class RemoveAttributeModifier(
    @SerialName("attribute-modifiers") val attributeModifiers: List<AttributeModifier>
) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false

        attributeModifiers.forEach { modifier ->
            val attribute = player.getAttribute(modifier.attribute) ?: return@forEach
            attribute.modifiers
                .filter { it.key == modifier.key }
                .forEach { attribute.removeModifier(it) }
        }

        return true
    }
}
