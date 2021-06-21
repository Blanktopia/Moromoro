package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.serializers.FormattedString

@Serializable
@SerialName("sudo-command")
data class SudoCommand(val command: String, val permissions: List<String>) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        val formatted = command.replace("%p", player.name)
        val permissions = permissions.map { player.addAttachment(Moromoro.plugin, it, true) }
        return try {
            player.performCommand(formatted)
        } finally {
            permissions.forEach { it.remove() }
        }
    }
}
