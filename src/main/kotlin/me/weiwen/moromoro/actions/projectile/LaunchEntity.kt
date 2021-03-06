package me.weiwen.moromoro.actions.projectile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.actions.disguise.DisguiseData
import me.weiwen.moromoro.actions.disguise.disguise
import me.weiwen.moromoro.extensions.customItemKey
import me.weiwen.moromoro.extensions.pitch
import org.bukkit.NamespacedKey
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.entity.Projectile
import org.bukkit.persistence.PersistentDataType

@Serializable
@SerialName("launch-entity")
data class LaunchEntity(
    val entity: EntityType,
    val magnitude: Double = 1.5,
    val pitch: Double = 0.0,
    val isPitchRelative: Boolean = true,
    val disguise: DisguiseData? = null,
) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        val entity = player.world.spawnEntity(player.eyeLocation, entity)

        ctx.item?.customItemKey?.let { key ->
            val persistentData = entity.persistentDataContainer
            persistentData.set(NamespacedKey(Moromoro.plugin.config.namespace, "type"), PersistentDataType.STRING, key)
        }

        if (entity is Projectile) {
            entity.shooter = player
            if (entity is AbstractArrow) {
                entity.pickupStatus = AbstractArrow.PickupStatus.CREATIVE_ONLY
            }
        }
        if (entity is Item) {
            entity.pickupDelay = 20
            entity.setCanMobPickup(false)
        }

        val v = player.location.direction

        if (!isPitchRelative) {
            v.pitch = 0.0
        }
        v.pitch += pitch
        v.normalize().multiply(magnitude)
        entity.velocity = v

        if (disguise != null) {
            val disguise = disguise.disguise
            disguise.entity = entity
            disguise.startDisguise()
        }

        return true
    }
}
