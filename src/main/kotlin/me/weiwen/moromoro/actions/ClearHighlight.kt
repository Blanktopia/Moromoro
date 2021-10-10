@file:UseSerializers(MaterialSerializer::class)

package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.extensions.clearHighlights
import me.weiwen.moromoro.serializers.MaterialSerializer

@Serializable
@SerialName("clear-highlight")
object ClearHighlight : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false

        player.clearHighlights()

        return true
    }
}
