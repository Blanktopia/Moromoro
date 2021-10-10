package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.extensions.pitch

@Serializable
@SerialName("launch-item-projectile")
data class LaunchItemProjectile(
    val magnitude: Double = 1.0,
    val pitch: Double = 0.0,
    val isPitchRelative: Boolean = true,
) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        val item = ctx.item ?: return false

        val v = player.location.direction

        if (!isPitchRelative) {
            v.pitch = 0.0
        }
        v.pitch += pitch
        v.normalize().multiply(magnitude)

        Moromoro.plugin.itemProjectileManager.createProjectile(player, v, item)

        return true
    }
}
