package me.weiwen.moromoro.recipes

import me.weiwen.moromoro.Moromoro.Companion.plugin
import me.weiwen.moromoro.extensions.customItemKey
import me.weiwen.moromoro.items.ItemManager
import org.bukkit.Keyed
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.FurnaceBurnEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.inventory.PrepareSmithingEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ComplexRecipe

object RecipeListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (plugin.config.discoverAllRecipes) {
            RecipeManager.recipes.keys.forEach {
                event.player.discoverRecipe(it)
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPrepareItemCraft(event: PrepareItemCraftEvent) {
        val recipe = event.recipe as? Keyed ?: return

        if (recipe is ComplexRecipe) {
            val key = event.inventory.result?.customItemKey

            if (key != null && ItemManager.templates[key]?.dyeable == true) {
                // Allow custom items to be dyed
                return
            }
        }

        if (recipe.key.namespace == plugin.config.namespace) {
            // Allow custom items to be used in plugin-defined recipes
            return
        }

        if (event.inventory.matrix?.filterNotNull()?.any { it.customItemKey != null } == true) {
            event.inventory.result = null
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onFurnaceBurn(event: FurnaceBurnEvent) {
        if (event.fuel.customItemKey != null) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPrepareAnvil(event: PrepareAnvilEvent) {
        val item = event.inventory.secondItem ?: return

        if (item.customItemKey != null) {
            event.result = null
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPrepareSmithing(event: PrepareSmithingEvent) {
        val equipment = event.inventory.inputEquipment
        val mineral = event.inventory.inputMineral

        if (equipment?.customItemKey != null || mineral?.customItemKey != null) {
            event.result = null
        }
    }
}