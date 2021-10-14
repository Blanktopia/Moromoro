package me.weiwen.moromoro.actions.command

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context

@Serializable
@SerialName("console-command")
data class ConsoleCommand(val command: String) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        val formatted = command.replace("%p", player.name)
        return player.server.dispatchCommand(player.server.consoleSender, formatted)
    }
}
