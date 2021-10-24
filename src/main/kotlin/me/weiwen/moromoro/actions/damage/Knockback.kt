package me.weiwen.moromoro.actions.damage

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import org.bukkit.util.Vector

@Serializable
@SerialName("knockback")
data class Knockback(val x: Double? = null, val y: Double? = null, val z: Double? = null) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        val entity = ctx.entity ?: ctx.player ?: return false

        val vec = Vector(x ?: 0.0, y ?: 0.0, z ?: 0.0)
        vec.rotateAroundY(player.location.yaw.toDouble())

        entity.velocity = vec

        return true
    }
}

