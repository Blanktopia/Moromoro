package me.weiwen.moromoro.actions.condition

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import org.bukkit.entity.LivingEntity

@Serializable
@SerialName("is-entity-damageable")
object IsEntityDamageable : Action {
    override fun perform(ctx: Context): Boolean {
        val entity = ctx.entity as? LivingEntity ?: return false
        return entity.noDamageTicks == 0
    }
}

