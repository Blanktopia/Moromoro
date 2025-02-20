package me.weiwen.moromoro.enchantments.listeners

import me.weiwen.moromoro.Moromoro.Companion.plugin
import me.weiwen.moromoro.extensions.playSoundAt
import me.weiwen.moromoro.extensions.spawnParticle
import org.bukkit.*
import org.bukkit.entity.Entity
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

object Frost : Listener {
    val key = NamespacedKey(plugin.config.namespace, "frost")

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        if (event.damage == 0.0) return

        val entity: Entity = event.entity
        val damager = event.damager
        if (event.cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK && event.cause != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) return

        if (entity is HumanEntity && entity.isBlocking) return

        if (entity !is LivingEntity || damager !is LivingEntity) return

        val weapon = damager.equipment?.itemInMainHand ?: return
        val enchantment = Registry.ENCHANTMENT.get(key) ?: return
        if (!weapon.containsEnchantment(enchantment)) return

        val level = weapon.getEnchantmentLevel(enchantment) ?: 1
        entity.spawnParticle(Particle.SNOWFLAKE, 20, 0.01)
        if (event.cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            entity.playSoundAt(Sound.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 0.5f, 0.1f)
            entity.playSoundAt(Sound.ENTITY_SNOW_GOLEM_HURT, SoundCategory.PLAYERS, 0.5f, 1.5f)
            entity.fireTicks = 0
            entity.freezeTicks = minOf(entity.maxFreezeTicks, 60 * level)
        }
    }
}
