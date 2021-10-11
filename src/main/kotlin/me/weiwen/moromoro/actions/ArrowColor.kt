@file:UseSerializers(ColorSerializer::class)

package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.serializers.ColorSerializer
import org.bukkit.Color
import org.bukkit.entity.Arrow
import org.bukkit.event.entity.EntityShootBowEvent

@Serializable
@SerialName("arrow-color")
data class ArrowColor(val color: Color) : Action {
    override fun perform(ctx: Context): Boolean {
        val event = ctx.event as? EntityShootBowEvent ?: return false
        val arrow = event.projectile as? Arrow ?: return false

        arrow.color = color

        return true
    }
}
