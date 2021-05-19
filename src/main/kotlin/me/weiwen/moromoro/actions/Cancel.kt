package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("cancel")
object Cancel : Action {
    override fun perform(ctx: Context): Boolean {
        ctx.isCancelled = true
        return true
    }
}

