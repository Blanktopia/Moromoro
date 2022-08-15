package me.weiwen.moromoro.actions.potioneffect

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.managers.PermanentPotionEffectManager.removePermanentPotionEffects

@Serializable
@SerialName("remove-permanent-potion-effect")
data class RemovePermanentPotionEffect(val key: String) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        player.removePermanentPotionEffects(key)
        return true
    }
}
