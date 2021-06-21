@file:UseSerializers(PotionEffectTypeSerializer::class)

package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.managers.addPermanentPotionEffects
import me.weiwen.moromoro.serializers.PotionEffectTypeSerializer
import org.bukkit.potion.PotionEffectType

@Serializable
@SerialName("add-permanent-potion-effect")
data class AddPermanentPotionEffect(val key: String, val effects: Map<PotionEffectType, Int>) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        player.addPermanentPotionEffects(key, effects)
        return true
    }
}
