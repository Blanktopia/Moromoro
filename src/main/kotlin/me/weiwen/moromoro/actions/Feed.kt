package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.extensions.playSoundAt
import org.bukkit.Sound
import org.bukkit.SoundCategory

@Serializable
@SerialName("feed")
data class Feed(val amount: Int, val saturation: Float) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player
        player.foodLevel += amount
        player.saturation += saturation
        return true
    }
}
