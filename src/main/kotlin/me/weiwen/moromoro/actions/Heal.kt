package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.extensions.playSoundAt
import org.bukkit.Sound
import org.bukkit.SoundCategory

@Serializable
@SerialName("feed")
data class Heal(val amount: Double) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player
        player.health += amount
        return true
    }
}
