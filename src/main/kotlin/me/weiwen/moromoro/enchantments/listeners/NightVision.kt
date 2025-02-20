package me.weiwen.moromoro.enchantments.listeners

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import me.weiwen.moromoro.Moromoro.Companion.plugin
import me.weiwen.moromoro.managers.PermanentPotionEffectManager.addPermanentPotionEffects
import me.weiwen.moromoro.managers.PermanentPotionEffectManager.removePermanentPotionEffects
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.potion.PotionEffectType

object NightVision : Listener {
    val key = NamespacedKey(plugin.config.namespace, "nightvision")

    @EventHandler
    fun onPlayerArmorChange(event: PlayerArmorChangeEvent) {
        val newItem = event.newItem
        val oldItem = event.oldItem
        val player = event.player
        val enchantment = Registry.ENCHANTMENT.get(key) ?: return
        if (newItem != null && newItem.containsEnchantment(enchantment)) {
            player.addPermanentPotionEffects("night_vision", mapOf(
                Pair(PotionEffectType.NIGHT_VISION, 0)
            ))
        } else if (oldItem != null && oldItem.containsEnchantment(enchantment)) {
            player.removePermanentPotionEffects("night_vision")
        }
    }
}
