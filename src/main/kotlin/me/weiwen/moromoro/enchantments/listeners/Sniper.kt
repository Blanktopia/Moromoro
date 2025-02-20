package me.weiwen.moromoro.enchantments.listeners

import me.weiwen.moromoro.Moromoro.Companion.plugin
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.entity.AbstractArrow
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent

object Sniper : Listener {
    val key = NamespacedKey(plugin.config.namespace, "sniper")

    @EventHandler
    fun onEntityShootBow(event: EntityShootBowEvent) {
        val item = event.entity.equipment?.itemInMainHand ?: return

        val enchantment = Registry.ENCHANTMENT.get(key) ?: return
        if (!item.containsEnchantment(enchantment)) return

        val level = item.getEnchantmentLevel(enchantment) ?: 1
        val boost =
            when (level) {
                1 -> 1.25
                2 -> 1.4
                3 -> 1.5
                else -> 1.5
            }

        val projectile = event.projectile
        if (projectile is AbstractArrow) {
            projectile.velocity = projectile.velocity.multiply(boost)
            projectile.damage = projectile.damage / boost
        }
    }
}
