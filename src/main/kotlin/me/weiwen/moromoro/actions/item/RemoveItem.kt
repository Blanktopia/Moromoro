package me.weiwen.moromoro.actions.item

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context

@Serializable
@SerialName("remove-item")
object RemoveItem : Action {
    override fun perform(ctx: Context): Boolean {
        ctx.removeItem = true
        return true
    }
}

