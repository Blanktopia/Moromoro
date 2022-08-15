@file:UseSerializers(PotionEffectTypeSerializer::class)

package me.weiwen.moromoro.actions.mechanic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.managers.FlyInClaimsListener.canFlyInClaims
import me.weiwen.moromoro.serializers.PotionEffectTypeSerializer

@Serializable
@SerialName("fly-in-claims")
data class FlyInClaims(val fly: Boolean) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        player.canFlyInClaims = fly
        player.setFlyingFallDamage(fly)
        return true
    }
}
