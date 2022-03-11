package me.weiwen.moromoro.actions.projectile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.actions.LocationSelector
import me.weiwen.moromoro.actions.location
import org.bukkit.entity.EntityType
import org.bukkit.entity.EvokerFangs

@Serializable
@SerialName("evoker-fang")
data class EvokerFang(val location: LocationSelector = LocationSelector.ENTITY) : Action {
    override fun perform(ctx: Context): Boolean {
        val location = location.location(ctx) ?: return false

        val fangs = location.world.spawnEntity(location, EntityType.EVOKER_FANGS) as EvokerFangs
        ctx.player?.let {
            fangs.owner = it
            fangs.location.yaw = it.location.yaw
        }

        return true
    }
}

