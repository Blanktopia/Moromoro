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
        val player = ctx.player ?: return false

        val vec = Vector(x, y, z)

        if (rotateYaw) {
            vec.rotateAroundY(-player.location.yaw.toDouble() * PI / 180)
        }
        if (rotatePitch) {
            vec.rotateAroundAxis(player.location.direction.crossProduct(Vector(0, 1, 0)), -player.location.pitch.toDouble() * PI / 180)
        }

        if (max != null) {
            if (player.velocity.lengthSquared() <= max * max) {
                player.velocity = player.velocity.add(vec).normalize().multiply(max)
            }
        } else {
            player.velocity = player.velocity.add(vec)
        }

        return true
    }
}

