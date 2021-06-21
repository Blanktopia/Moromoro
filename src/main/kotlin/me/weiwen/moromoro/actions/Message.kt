package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.serializers.FormattedString

@Serializable
@SerialName("message")
data class Message(val message: FormattedString) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        player.sendMessage(message.value)
        return true
    }
}
