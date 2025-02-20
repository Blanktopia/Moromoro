package me.weiwen.moromoro.actions.velocity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import org.bukkit.util.Vector

@Serializable
@SerialName("set-velocity")
data class SetVelocity(val x: Double? = null, val y: Double? = null, val z: Double? = null) : Action {
    override fun perform(ctx: Context): Boolean {
        val entity = ctx.entity ?: ctx.player ?: return false

        val vec = Vector(x ?: entity.velocity.x, y ?: entity.velocity.y, z ?: entity.velocity.z)
        vec.rotateAroundY(entity.location.yaw.toDouble())

        entity.velocity = vec

        return true
    }
}

