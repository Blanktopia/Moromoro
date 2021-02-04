package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Sound
import org.bukkit.SoundCategory

@Serializable
@SerialName("play-sound")
data class PlaySound(val sound: Sound, val pitch: Float = 1.0f, val volume: Float = 1.0f) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player
        player.world.playSound(player.location, sound, SoundCategory.PLAYERS, volume, pitch)
        return true
    }
}
