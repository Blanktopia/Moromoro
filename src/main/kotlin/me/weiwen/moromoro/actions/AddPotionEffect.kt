@file:UseSerializers(PotionEffectTypeSerializer::class)

package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.serializers.PotionEffectTypeSerializer
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

@Serializable
@SerialName("add-potion-effect")
data class AddPotionEffect(val effect: PotionEffectType, val duration: Int, val level: Int) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player
        player.addPotionEffect(PotionEffect(effect, duration, level))
        return true
    }
}
