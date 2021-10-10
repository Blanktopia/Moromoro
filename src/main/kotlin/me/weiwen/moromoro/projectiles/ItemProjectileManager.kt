package me.weiwen.moromoro.projectiles

import me.weiwen.moromoro.Moromoro
import org.bukkit.NamespacedKey
import org.bukkit.entity.*
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.EulerAngle
import org.bukkit.util.Vector

class ItemProjectileManager(val plugin: Moromoro) {
    val projectiles: MutableList<ArmorStand?> = mutableListOf()

    fun enable() {
        projectiles.clear()
    }

    fun disable() {}

    fun createProjectile(shooter: LivingEntity, vector: Vector, itemStack: ItemStack) {
        val location = shooter.eyeLocation.clone().add(0.0, -0.6, 0.0)

        val armorStand = (shooter.world.spawnEntity(location.clone().set(0.0, 0.0, 0.0), EntityType.ARMOR_STAND) as ArmorStand).apply {
            isVisible = false
            isSmall = true
            setItem(EquipmentSlot.HAND, itemStack)
            persistentDataContainer.set(NamespacedKey(plugin.config.namespace, "item"), PersistentDataType.BYTE_ARRAY, itemStack.serializeAsBytes())
        }

        this.projectiles.add(armorStand)

        armorStand.teleport(location)
        this.tickProjectile(shooter, armorStand, vector.multiply(1.2), 0, itemStack)
    }

    private fun tickProjectile(shooter: LivingEntity, armorStand: ArmorStand, vector: Vector, recurse: Int, itemStack: ItemStack) {
        if (recurse < 300) {
            val nearbyEntities = armorStand.getNearbyEntities(1.0, 1.0, 1.0).iterator()
            while (nearbyEntities.hasNext()) {
                val e = nearbyEntities.next() as Entity
                if (e !== shooter && e !== armorStand && e is Damageable) {
                    // TODO: on hit
                    projectiles.remove(armorStand)
                    armorStand.remove()
                    break
                }
            }

            val x = armorStand.rightArmPose.x
            armorStand.rightArmPose = EulerAngle(x + 0.3 * plugin.config.projectileTickInterval, 0.0, 0.0)

            val vec = Vector(vector.x, vector.y - 0.015 * plugin.config.projectileTickInterval, vector.z)
            armorStand.velocity = vec

            if (!armorStand.isDead && armorStand.isOnGround) {
                // TODO: on hit ground
                projectiles.remove(armorStand)
                armorStand.remove()
            } else if (!armorStand.isDead) {
                plugin.server.scheduler.scheduleSyncDelayedTask(plugin, { ->
                    tickProjectile(shooter, armorStand, vec, recurse + 1, itemStack)
                }, plugin.config.projectileTickInterval)
            }
        } else {
            // TODO: on expire
            this.projectiles.remove(armorStand)
            armorStand.remove()
        }
    }
}