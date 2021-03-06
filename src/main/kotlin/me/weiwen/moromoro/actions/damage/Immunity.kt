package me.weiwen.moromoro.actions.damage

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import org.bukkit.event.entity.EntityDamageEvent

@Serializable
@SerialName("immunity")
data class Immunity(val causes: List<EntityDamageEvent.DamageCause>) : Action {
    override fun perform(ctx: Context): Boolean {
        val event = ctx.event as? EntityDamageEvent ?: return false

        if (event.cause in causes) {
            ctx.isCancelled = true
            return true
        } else {
            return false
        }
    }
}

