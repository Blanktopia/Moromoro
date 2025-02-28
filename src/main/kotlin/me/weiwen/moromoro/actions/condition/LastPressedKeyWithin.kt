package me.weiwen.moromoro.actions.condition

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import java.util.*

val lastPressed: MutableMap<UUID, MutableMap<InputKey, Long>> = mutableMapOf()

@Serializable
@SerialName("last-pressed-key-within")
data class LastPressedKeyWithin(val key: InputKey, val millis: Long) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        val lastPressed = lastPressed.get(player.uniqueId)?.get(key) ?: return false
        return System.currentTimeMillis() < lastPressed + millis
    }
}

