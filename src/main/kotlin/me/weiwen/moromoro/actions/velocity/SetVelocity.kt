package me.weiwen.moromoro.actions.velocity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import org.bukkit.util.Vector
import kotlin.math.PI

@Serializable
@SerialName("set-velocity")
data class SetVelocity(
    val x: Double? = null,
    val y: Double? = null,
    val z: Double? = null,
    @SerialName("rotate-yaw")
    val rotateYaw: Boolean = true,
    @SerialName("rotate-pitch")
    val rotatePitch: Boolean = false
) : Action {
    override fun perform(ctx: Context): Boolean {
        val entity = ctx.entity ?: ctx.player ?: return false

        val vec = Vector(x ?: entity.velocity.x, y ?: entity.velocity.y, z ?: entity.velocity.z)

        if (rotateYaw) {
            vec.rotateAroundY(-entity.location.yaw.toDouble() * PI / 180)
        }
        if (rotatePitch) {
            vec.rotateAroundAxis(entity.location.direction.crossProduct(Vector(0, 1, 0)), -entity.location.pitch.toDouble() * PI / 180)
        }

        entity.velocity = vec

        return true
    }
}

