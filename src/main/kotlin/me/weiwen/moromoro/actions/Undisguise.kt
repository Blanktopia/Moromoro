package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.libraryaddict.disguise.DisguiseAPI

@Serializable
@SerialName("undisguise")
object Undisguise : Action {
    override fun perform(ctx: Context): Boolean {
        DisguiseAPI.undisguiseToAll(ctx.player)
        return true
    }
}

