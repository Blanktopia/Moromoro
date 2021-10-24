package me.weiwen.moromoro.actions.effect

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.actions.LocationSelector
import me.weiwen.moromoro.extensions.playSoundAt
import org.bukkit.SoundCategory

@Serializable
@SerialName("play-sound")
data class PlaySound(
    val location: LocationSelector = LocationSelector.PLAYER,
    val sound: String,
    val category: SoundCategory = SoundCategory.BLOCKS,
    val pitch: Float = 1.0f,
    val volume: Float = 1.0f
) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        player.playSoundAt(sound, category, volume, pitch)
        return true
    }
}
