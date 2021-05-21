package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.Moromoro
import org.bukkit.util.Vector

@Serializable
@SerialName("add-velocity")
data class AddVelocity(val x: Double, val y: Double, val z: Double) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player

        val vec = Vector(x, y, z)
        vec.rotateAroundY(player.location.yaw.toDouble())

        player.velocity = player.velocity.add(vec)

        return true
    }
}
