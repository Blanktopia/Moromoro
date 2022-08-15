@file:UseSerializers(PotionEffectTypeSerializer::class)

package me.weiwen.moromoro.actions.mechanic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.managers.ExperienceBoostListener.addExperienceBoost
import me.weiwen.moromoro.serializers.PotionEffectTypeSerializer

@Serializable
@SerialName("experience-boost")
data class ExperienceBoost(val multiplier: Double, val duration: Int) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        player.addExperienceBoost(multiplier, duration)
        return true
    }
}
