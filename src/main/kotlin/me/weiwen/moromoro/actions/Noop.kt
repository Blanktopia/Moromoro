package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("noop")
object Noop : Action {
    override fun perform(ctx: Context): Boolean {
        return true
    }
}

