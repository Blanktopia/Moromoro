package me.weiwen.moromoro.enchantments.listeners

import me.weiwen.moromoro.Moromoro.Companion.plugin
import me.weiwen.moromoro.extensions.spawnParticle
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.Registry
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

object Harvest : Listener {
    val key = NamespacedKey(plugin.config.namespace, "harvest")
    private val HARVESTABLE_BLOCKS = setOf(Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.BEETROOTS, Material.COCOA, Material.NETHER_WART)
    private val HARVESTABLE_ITEMS = setOf(Material.WHEAT_SEEDS, Material.CARROT, Material.POTATO, Material.BEETROOT_SEEDS, Material.COCOA_BEANS, Material.NETHER_WART)

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val tool = player.inventory.itemInMainHand
        val enchantment = Registry.ENCHANTMENT.get(key) ?: return
        if (!tool.containsEnchantment(enchantment)) return

        val blockData = event.block.blockData as? Ageable ?: return
        if (!HARVESTABLE_BLOCKS.contains(event.block.type)) return

        event.isCancelled = true

        if (blockData.age < blockData.maximumAge) return

        for (drop in event.block.getDrops(tool)) {
            if (HARVESTABLE_ITEMS.contains(drop.type)) {
                drop.amount -= 1
            }
            if (drop.amount > 0) {
                event.block.world.dropItemNaturally(event.block.location, drop)
            }
        }

        event.block.spawnParticle(Particle.HAPPY_VILLAGER, 4, 0.01)
        blockData.age = 0
        event.block.blockData = blockData
    }
}
