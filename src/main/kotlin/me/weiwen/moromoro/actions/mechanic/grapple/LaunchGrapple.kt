@file:UseSerializers(ColorSerializer::class)

package me.weiwen.moromoro.actions.mechanic.grapple

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.extensions.customItemKey
import me.weiwen.moromoro.extensions.playSoundAt
import me.weiwen.moromoro.managers.ProjectileManager
import me.weiwen.moromoro.serializers.ColorSerializer
import org.bukkit.NamespacedKey
import org.bukkit.SoundCategory
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.persistence.PersistentDataType
import java.lang.ref.WeakReference
import java.util.*

val grapples: MutableMap<UUID, WeakReference<Arrow>> = mutableMapOf()

@Serializable
@SerialName("launch-grapple")
data class LaunchGrapple(
    val speed: Double = 1.0
) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false

        grapples[player.uniqueId]?.let {
            val arrow = it.get()
            if (arrow != null && !arrow.isDead) {
                arrow.remove()
            }
        }

        val entity = (player.world.spawnEntity(player.eyeLocation, EntityType.ARROW)) as Arrow
        grapples[player.uniqueId] = WeakReference(entity)

        ctx.item?.customItemKey?.let { key ->
            val persistentData = entity.persistentDataContainer
            persistentData.set(NamespacedKey(Moromoro.plugin.config.namespace, "type"), PersistentDataType.STRING, key)
        }

        entity.shooter = player
        entity.pickupStatus = AbstractArrow.PickupStatus.DISALLOWED
        entity.damage = 0.0

        val v = player.location.direction

        entity.velocity = v.normalize().multiply(speed)

        ctx.item?.customItemKey?.let {
            ProjectileManager.register(entity, ctx.player?.uniqueId, it)
        }

        player.playSoundAt("entity.iron_golem.hurt", SoundCategory.PLAYERS, 0.5f, 2.0f)
        player.playSoundAt("block.iron_door.open", SoundCategory.PLAYERS, 0.5f, 1.0f)
        player.playSoundAt("entity.skeleton.shoot", SoundCategory.PLAYERS, 0.5f, 0.8f)

        return true
    }
}
