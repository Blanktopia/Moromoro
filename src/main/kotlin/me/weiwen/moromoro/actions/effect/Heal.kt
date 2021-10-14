package me.weiwen.moromoro.actions.effect

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import org.bukkit.entity.LivingEntity

@Serializable
@SerialName("heal")
data class Heal(val amount: Double) : Action {
    override fun perform(ctx: Context): Boolean {
        val entity = ctx.entity as? LivingEntity ?: ctx.player ?: return false
        entity.health += amount
        return true
    }
}
