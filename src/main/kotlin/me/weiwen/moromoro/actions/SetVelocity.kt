package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.util.Vector

@Serializable
@SerialName("set-velocity")
data class SetVelocity(val x: Double?, val y: Double?, val z: Double?) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false

        val vec = Vector(x ?: 0.0, y ?: 0.0, z ?: 0.0)
        vec.rotateAroundY(player.location.yaw.toDouble())

        player.velocity = vec

        return true
    }
}

