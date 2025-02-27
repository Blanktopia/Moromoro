package me.weiwen.moromoro.actions.mechanic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.extensions.playSoundAt
import me.weiwen.moromoro.extensions.spawnParticle
import me.weiwen.moromoro.extensions.spawnParticleLine
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.SoundCategory
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageEvent

@Serializable
@SerialName("lightning-sword")
data class LightningSword(val radius: Double, val bounces: Int, @SerialName("chain-damage") val chainDamage: Double) :
    Action {
    override fun perform(ctx: Context): Boolean {
        val event = ctx.event as? EntityDamageEvent ?: return false
        val entity = event.entity as? LivingEntity ?: return false
        val player = ctx.player ?: return false

        if (event.cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return false
        }

        if (event.damage == 0.0) {
            return false
        }

        entity.location.playSoundAt("entity.lightning_bolt.thunder", SoundCategory.PLAYERS, 0.5f, 2.0f)

        val others =
            entity.location.getNearbyLivingEntities(radius).filter { it != entity && it != player }.take(bounces)
                .toMutableSet()

        var prev = entity.location.clone().add(0.0, entity.height / 2, 0.0)
        var i = 1
        while (others.isNotEmpty()) {
            val other = others.minByOrNull { it.location.distanceSquared(prev) } ?: return true
            others.remove(other)

            Bukkit.getServer().scheduler.scheduleSyncDelayedTask(Moromoro.plugin, {
                if (other.isDead) {
                    return@scheduleSyncDelayedTask
                }

                other.damage(chainDamage)

                val curr = other.location.clone().add(0.0, other.height / 2, 0.0)
                curr.spawnParticle(Particle.SOUL_FIRE_FLAME, 10, 0.4, 0.0)
                curr.spawnParticle(Particle.FLASH, 5.0, 0.0, 0.0, 0.0)
                prev.spawnParticleLine(
                    curr,
                    Particle.ELECTRIC_SPARK,
                    0.1,
                    0.1,
                    1,
                    0.2
                )
                other.location.playSoundAt("entity.lightning_bolt.thunder", SoundCategory.PLAYERS, 0.5f, 2.0f)

                prev = curr
            }, (5 * i).toLong())

            i++
        }

        return true
    }
}

