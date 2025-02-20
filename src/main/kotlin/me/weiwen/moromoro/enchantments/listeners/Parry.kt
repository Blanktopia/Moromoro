package me.weiwen.moromoro.enchantments.listeners

import me.weiwen.moromoro.Moromoro.Companion.plugin
import me.weiwen.moromoro.extensions.spawnParticle
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.Registry
import org.bukkit.entity.Entity
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object Parry : Listener {
    val key = NamespacedKey(plugin.config.namespace, "parry")

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        val entity: Entity = event.entity
        if (entity !is HumanEntity || !entity.isBlocking) return

        val weapon = entity.equipment.itemInOffHand
        val enchantment = Registry.ENCHANTMENT.get(key) ?: return
        if (!weapon.containsEnchantment(enchantment)) return

        val level = weapon.getEnchantLevel(enchantment)
        entity.spawnParticle(Particle.CRIT, 10, 0.01)

        if (event.isCancelled) return

        entity.addPotionEffect(PotionEffect(PotionEffectType.STRENGTH, 40, level - 1))
    }
}

