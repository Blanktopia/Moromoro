package me.weiwen.moromoro.managers

import me.weiwen.moromoro.Manager
import me.weiwen.moromoro.Moromoro.Companion.plugin
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitTask
import java.util.*

object PermanentPotionEffectManager : Manager {
    private var task: BukkitTask? = null

    private val potionEffectGroups: MutableMap<UUID, MutableMap<String, Map<PotionEffectType, Int>>> = mutableMapOf()

    override fun enable() {
        task = plugin.server.scheduler.runTaskTimer(plugin, ::applyToAllPlayers as (() -> Unit), 100, 100)
    }

    override fun disable() {
        task?.cancel()
    }

    private fun applyToAllPlayers() {
        for (world in plugin.server.worlds) {
            // DungeonsXL compatibility
            if (world.name.startsWith("DXL_Game_")) {
                continue
            }
            for (player in world.players) {
                val playerPotionEffectGroups = potionEffectGroups[player.uniqueId] ?: continue
                for (potionEffects in playerPotionEffectGroups.values) {
                    for ((type, level) in potionEffects.entries) {
                        player.addPotionEffect(
                            PotionEffect(
                                type,
                                619,
                                level,
                                true,
                                false,
                            )
                        )
                    }
                }
            }
        }
    }

    fun Player.addPermanentPotionEffects(key: String, effects: Map<PotionEffectType, Int>) {
        potionEffectGroups.getOrPut(uniqueId) { mutableMapOf() }[key] = effects
        for ((type, level) in effects.entries) {
            addPotionEffect(
                PotionEffect(
                    type,
                    619,
                    level,
                    true,
                    false,
                )
            )
        }
    }

    fun Player.removePermanentPotionEffects(key: String) {
        val effects = potionEffectGroups[uniqueId]?.get(key) ?: return
        for ((type, level) in effects.entries) {
            if (getPotionEffect(type)?.amplifier == level) {
                removePotionEffect(type)
            }
        }
        potionEffectGroups[uniqueId]?.remove(key)
    }
}
