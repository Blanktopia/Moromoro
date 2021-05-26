package me.weiwen.moromoro.listeners

import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.extensions.customItemKey
import org.bukkit.Keyed
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.*
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ComplexRecipe
import java.util.logging.Level

class RecipeListener(val plugin: Moromoro) : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (plugin.config.discoverAllRecipes) {
            plugin.recipeManager.recipes.keys.forEach {
                event.player.discoverRecipe(it)
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPrepareItemCraft(event: PrepareItemCraftEvent) {
        val recipe = event.recipe as? Keyed ?: return

        if (recipe is ComplexRecipe) {
            // Allow custom items to be dyed
            return
        }

        if (recipe.key.namespace == plugin.config.namespace) {
            // Allow custom items to be used in plugin-defined recipes
            return
        }

        if (event.inventory.matrix.filterNotNull().any { it.customItemKey != null }) {
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