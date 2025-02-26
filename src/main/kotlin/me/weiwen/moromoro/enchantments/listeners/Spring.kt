package me.weiwen.moromoro.enchantments.listeners

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import me.weiwen.moromoro.Moromoro.Companion.plugin
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object Spring : Listener {
    val key = NamespacedKey(plugin.config.namespace, "spring")

    @EventHandler
    fun onPlayerJump(event: PlayerJumpEvent) {
        val enchantment = Registry.ENCHANTMENT.get(key) ?: return
        for (item in event.player.inventory.armorContents) {
            item ?: continue
            if (!item.containsEnchantment(enchantment)) continue

            val level = item.getEnchantmentLevel(enchantment)
            val prevLevel = event.player.getPotionEffect(PotionEffectType.JUMP_BOOST)?.amplifier ?: -1
            val amplifier = minOf(level - 1, prevLevel + 1)
            event.player.addPotionEffect(
                PotionEffect(
                    PotionEffectType.JUMP_BOOST,
                    20 + 10 * amplifier,
                    amplifier,
                    true
                )
            )
        }
    }
}
