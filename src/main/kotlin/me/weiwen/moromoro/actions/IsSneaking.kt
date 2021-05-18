package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("is-sneaking")
object IsSneaking : Action {
    override fun perform(ctx: Context): Boolean {
        return ctx.player.isSneaking
    }
}

