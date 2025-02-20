package me.weiwen.moromoro.enchantments.listeners

import me.weiwen.moromoro.Moromoro.Companion.plugin
import org.bukkit.NamespacedKey
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

object Spectral : Listener {
    val key = NamespacedKey(plugin.config.namespace, "spectral")

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        val entity: Entity = event.entity
        val damager = event.damager
        if (event.cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK && event.cause != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) return

        if (entity is HumanEntity && entity.isBlocking) return

        if (event.isCancelled) return

        if (entity !is LivingEntity || damager !is LivingEntity) return

        val weapon = damager.equipment?.itemInMainHand ?: return
        val enchantment = Registry.ENCHANTMENT.get(key) ?: return
        if (!weapon.containsEnchantment(enchantment)) return

        val level = weapon.getEnchantmentLevel(enchantment) ?: 1
        entity.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 20 + level * 20, 0, true))
    }
}
