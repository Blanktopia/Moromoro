package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.serializers.FormattedString

@Serializable
@SerialName("action-bar")
data class ActionBar(val message: FormattedString) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player
        player.sendActionBar(message.value)
        return true
    }
}
