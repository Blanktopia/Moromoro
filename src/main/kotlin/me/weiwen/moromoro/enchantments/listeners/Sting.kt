package me.weiwen.moromoro.enchantments.listeners

import me.weiwen.moromoro.Moromoro.Companion.plugin
import me.weiwen.moromoro.extensions.spawnParticle
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.Registry
import org.bukkit.entity.Entity
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object Sting : Listener {
    val key = NamespacedKey(plugin.config.namespace, "sting")

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
        entity.spawnParticle(Particle.SNEEZE, 10, 0.01)
        entity.addPotionEffect(PotionEffect(PotionEffectType.POISON, 80,
            when (level) {
                0, 1 -> 0
                2 -> 1
                3 -> 4
                else -> 4
            })
        )
    }
}
