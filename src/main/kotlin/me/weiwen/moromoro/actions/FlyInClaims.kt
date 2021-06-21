@file:UseSerializers(PotionEffectTypeSerializer::class)

package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.managers.addExperienceBoost
import me.weiwen.moromoro.managers.canFlyInClaims
import me.weiwen.moromoro.serializers.PotionEffectTypeSerializer
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

@Serializable
@SerialName("fly-in-claims")
data class FlyInClaims(val fly: Boolean) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        player.canFlyInClaims = fly
        return true
    }
}
