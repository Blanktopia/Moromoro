package me.weiwen.moromoro.enchantments.listeners

import me.weiwen.moromoro.Moromoro.Companion.plugin
import me.weiwen.moromoro.extensions.playSoundAt
import me.weiwen.moromoro.extensions.spawnParticle
import org.bukkit.*
import org.bukkit.entity.EntityType
import org.bukkit.entity.ExperienceOrb
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.ItemStack
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.random.Random


data class SmeltResult(val item: ItemStack, val experience: Float)

object Smelt : Listener {
    val key = NamespacedKey(plugin.config.namespace, "smelt")

    val smeltRecipes: Map<Material, FurnaceRecipe> by lazy {
        val map = mutableMapOf<Material, FurnaceRecipe>()
        plugin.server.recipeIterator().forEach {
            if (it is FurnaceRecipe) {
                map[it.input.type] = it
            }
        }
        map
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onBlockDropItem(event: BlockDropItemEvent) {
        val player = event.player
        val tool = player.inventory.itemInMainHand
        val enchantment = Registry.ENCHANTMENT.get(key) ?: return
        if (!tool.containsEnchantment(enchantment)) return

        val block = event.block
        var smelted = false
        var experience = 0.0
        val items = event.items
        for (item in items) {
            val smeltedItem = getSmeltedDrops(item.itemStack) ?: continue
            smelted = true
            experience += smeltedItem.experience

            val itemStack = item.itemStack
            itemStack.type = smeltedItem.item.type
            item.itemStack = itemStack
        }

        if (smelted) {
            block.spawnParticle(Particle.FLAME, 1, 0.01)
            block.playSoundAt(Sound.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 0.5f, 0.8f)
        }

        if (experience > 0) {
            val orb = block.world.spawnEntity(block.location, EntityType.EXPERIENCE_ORB) as ExperienceOrb
            orb.experience = if (Random.nextDouble() > experience.rem(1.0)) {
                ceil(experience)
            } else {
                floor(experience)
            }.toInt()
        }
    }

    private fun getSmeltedDrops(item: ItemStack): SmeltResult? {
        if (item.type in BLACKLISTED_ITEMS) {
            return null
        }

        val recipe = smeltRecipes[item.type] ?: return null

        val result = recipe.result.clone().apply {
            amount *= item.amount
        }

        val experience = if (recipe.experience <= 0.15) {
            0f
        } else {
            recipe.experience * item.amount
        }

        return SmeltResult(result, experience)
    }
}


val BLACKLISTED_ITEMS = setOf<Material>()