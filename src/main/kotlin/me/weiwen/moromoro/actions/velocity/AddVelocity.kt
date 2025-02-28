package me.weiwen.moromoro.actions.velocity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import org.bukkit.util.Vector
import kotlin.math.PI

@Serializable
@SerialName("add-velocity")
data class AddVelocity(
    val x: Double = 0.0,
    val y: Double = 0.0,
    val z: Double = 0.0,
    val max: Double?,
    @SerialName("rotate-yaw")
    val rotateYaw: Boolean = true,
    @SerialName("rotate-pitch")
    val rotatePitch: Boolean = false
) : Action {
    override fun perform(ctx: Context): Boolean {
        val entity = ctx.entity ?: ctx.player ?: return false

        val vec = Vector(x, y, z)

        if (rotateYaw) {
            vec.rotateAroundY(-entity.location.yaw.toDouble() * PI / 180)
        }
        if (rotatePitch) {
            vec.rotateAroundAxis(entity.location.direction.crossProduct(Vector(0, 1, 0)), -entity.location.pitch.toDouble() * PI / 180)
        }

        if (max != null) {
            if (entity.velocity.lengthSquared() <= max * max) {
                entity.velocity = entity.velocity.add(vec).normalize().multiply(max)
            }
        } else {
            entity.velocity = entity.velocity.add(vec)
        }

        return true
    }
}

