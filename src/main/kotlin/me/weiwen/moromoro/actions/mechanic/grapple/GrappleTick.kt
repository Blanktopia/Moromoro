package me.weiwen.moromoro.actions.mechanic.grapple

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.extensions.playSoundAt
import me.weiwen.moromoro.extensions.spawnParticleLine
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.SoundCategory
import org.bukkit.entity.Arrow
import org.bukkit.util.Vector
import kotlin.math.sqrt

@Serializable
@SerialName("grapple-tick")
data class GrappleTick(
    val acceleration: Double = 0.5,
    @SerialName("max-speed") val maxSpeed: Double = 1.0,
    @SerialName("detach-speed") val detachSpeed: Double = 1.0,
    val range: Double = 50.0,
    val particle: Particle = Particle.ELECTRIC_SPARK
) :
    Action {
    private val rangeSquared = range * range

    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        val arrow = ctx.projectile as? Arrow ?: return false

        if (player.location.world != arrow.location.world) {
            arrow.remove()
            return false
        }

        val distanceSquared = arrow.location.distanceSquared(player.location.clone().add(0.0, player.height/2, 0.0))

        if (!arrow.isOnGround) {
            if (distanceSquared > rangeSquared) {
                player.playSoundAt("block.wool.break", SoundCategory.PLAYERS, 1.0f, 2.0f)
                arrow.remove()
            }
            if (player.isSneaking) {
                arrow.remove()
            }
            return true
        }

        arrow.ticksLived = 1

        val vec = player.velocity.add(
            arrow.location.toVector()
                .subtract(player.location.toVector())
                .normalize()
                .add(player.location.direction.multiply(0.75))
                .normalize()
                .multiply(acceleration)
        )
        val len = vec.lengthSquared()
        if (len > distanceSquared) {
            vec.normalize().multiply(sqrt(distanceSquared))
        }
        if (len > maxSpeed) {
            vec.normalize().multiply(maxSpeed)
        }
        player.velocity = vec
        player.fallDistance = 0f

        if (distanceSquared < 1.0 || player.isSneaking) {
            player.playSoundAt("entity.blaze.hurt", SoundCategory.PLAYERS, 0.5f, 2.0f)
            player.velocity = player.location.direction.multiply(detachSpeed).add(Vector(0.0, 0.5, 0.0))
            arrow.remove()
            return true
        }

        if (Bukkit.getServer().currentTick.mod(2) == 0) {
            player.playSoundAt("entity.fishing_bobber.retrieve", SoundCategory.PLAYERS, 1.0f, 0.5f)
            arrow.location.spawnParticleLine(player.eyeLocation, particle, 0.8)
        }

        return true
    }
}

