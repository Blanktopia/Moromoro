package me.weiwen.moromoro.actions.disguise

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.libraryaddict.disguise.DisguiseAPI
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context

@Serializable
@SerialName("undisguise")
object Undisguise : Action {
    override fun perform(ctx: Context): Boolean {
        DisguiseAPI.undisguiseToAll(ctx.player)
        return true
    }
}

