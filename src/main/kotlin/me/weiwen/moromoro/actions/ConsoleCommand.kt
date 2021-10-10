package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("console-command")
data class ConsoleCommand(val command: String) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        val formatted = command.replace("%p", player.name)
        return player.server.dispatchCommand(player.server.consoleSender, formatted)
    }
}
