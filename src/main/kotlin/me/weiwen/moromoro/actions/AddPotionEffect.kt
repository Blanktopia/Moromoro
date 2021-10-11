@file:UseSerializers(PotionEffectTypeSerializer::class)

package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.serializers.PotionEffectTypeSerializer
import org.bukkit.entity.LivingEntity
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

@Serializable
@SerialName("add-potion-effect")
data class AddPotionEffect(val effect: PotionEffectType, val duration: Int, val level: Int) : Action {
    override fun perform(ctx: Context): Boolean {
        val entity = ctx.entity as? LivingEntity ?: ctx.player ?: return false
        entity.addPotionEffect(PotionEffect(effect, duration, level))
        return true
    }
}
