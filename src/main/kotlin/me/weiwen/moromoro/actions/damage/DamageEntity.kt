package me.weiwen.moromoro.actions.damage

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import org.bukkit.entity.LivingEntity

@Serializable
@SerialName("damage-entity")
data class DamageEntity(val damage: Double) : Action {
    override fun perform(ctx: Context): Boolean {
        val entity = ctx.entity as? LivingEntity ?: ctx.player ?: return false

        entity.damage(damage, ctx.player)

        return true
    }
}

