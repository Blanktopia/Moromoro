package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.extensions.customItemKey
import me.weiwen.moromoro.extensions.playSoundAt
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.Arrow
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.persistence.PersistentDataType

@Serializable
@SerialName("arrow-volley")
data class ArrowVolley(val count: Int, val delay: Long) : Action {
    override fun perform(ctx: Context): Boolean {
        val event = ctx.event as? EntityShootBowEvent ?: return false
        val player = ctx.player ?: return false
        val arrow = event.projectile as? Arrow ?: return false
        val key = arrow.customItemKey ?: return false

        val speed = arrow.velocity.length()

        (1 until count).forEach { i ->
            Moromoro.plugin.server.scheduler.scheduleSyncDelayedTask(Moromoro.plugin, {
                val projectile = player.launchProjectile(Arrow::class.java, player.location.direction.multiply(speed)).apply {
                    pickupStatus = arrow.pickupStatus
                    isCritical = arrow.isCritical
                }

                val persistentData = projectile.persistentDataContainer
                persistentData.set(NamespacedKey(Moromoro.plugin.config.namespace, "type"), PersistentDataType.STRING, key)

                player.playSoundAt(Sound.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.0f)
            }, i * delay)
        }

        return true
    }
}
