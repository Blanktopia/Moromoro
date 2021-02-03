package me.weiwen.monogoto.actions

import org.bukkit.Sound
import org.bukkit.SoundCategory

class PlaySoundAction(private val sound: Sound, pitch: Float?, volume: Float?) : Action {
    private val pitch = pitch ?: 1.0f
    private val volume = volume ?: 1.0f

    override fun perform(ctx: Context): Boolean {
        val player = ctx.player
        player.world.playSound(player.location, sound, SoundCategory.PLAYERS, volume, pitch)
        return true
    }
}