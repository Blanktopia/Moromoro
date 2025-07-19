package me.weiwen.moromoro.blocks

import com.sk89q.worldedit.world.registry.Registries
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.extensions.*
import me.weiwen.moromoro.items.ItemManager
import me.weiwen.moromoro.items.ItemTemplate
import me.weiwen.moromoro.items.item
import net.kyori.adventure.key.Key
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.ExperienceOrb
import org.bukkit.inventory.ItemStack
import kotlin.math.ceil
import kotlin.math.max
import kotlin.random.Random

sealed interface CustomBlock {
    companion object {
        fun fromBlock(block: Block): CustomBlock? {
            return MushroomCustomBlock.fromBlock(block) ?: EntityCustomBlock.fromBlock(block)
        }
    }

    val block: Block
    val key: String

    val template: ItemTemplate?
        get() = ItemManager.templates[key]

    fun breakDuration(tool: ItemStack): Long {
        val speed = when (tool.type) {
            in goldTools -> 12.0
            in netheriteTools -> 9.0
            in diamondTools -> 8.0
            in ironTools -> 6.0
            in stoneTools -> 4.0
            in woodenTools -> 2.0
            Material.SHEARS -> 1.5
            else -> 1.0
        }

        val efficiency = when (val level = tool.getEnchantmentLevel(Enchantment.EFFICIENCY)) {
            0 -> 0
            else -> level * level + 1
        }

        val multiplier = template?.block?.tools?.let { tools ->
            if (tools.any { it.type == tool.type }) {
                speed + efficiency
            } else {
                1.0
            }
        } ?: (speed + efficiency)

        val hardness = template?.block?.hardness ?: 1.0
        val damage = template?.block?.tools?.let { tools ->
            if (tools.any { it.type == tool.type }) {
                multiplier / hardness / 30.0
            } else {
                1.0 / hardness / 100.0
            }
        } ?: (multiplier / hardness / 30.0)

        if (damage > 1.0) {
            return 0
        }

        return ceil(1.0 / damage).toLong()
    }

    fun breakNaturally(tool: ItemStack?, dropItem: Boolean, location: Location? = null): Boolean {
        val template = ItemManager.templates[key] ?: return false

        val location = location ?: block.location.add(0.5, 0.5, 0.5)

        if (dropItem) {
            val items = if (tool?.enchantments?.get(Enchantment.SILK_TOUCH) == null) {
                val experience = template.block?.experience ?: 0
                if (experience != 0) {
                    (location.world.spawnEntity(location, EntityType.EXPERIENCE_ORB) as? ExperienceOrb)?.apply {
                        this.experience = experience
                    }
                }
                template.block?.drops?.mapNotNull {
                    Registry.ITEM.get(Key.key(it))?.createItemStack() ?: ItemManager.templates[it]?.item(it, 1)
                } ?: listOf(template.item(key, 1))
            } else {
                listOf(template.item(key, 1))
            }

            if (template.block?.canFortune == true) {
                val fortune = tool?.enchantments?.get(Enchantment.FORTUNE) ?: 0
                items.forEach {
                    val multiplier = 1 + max(0, Random.nextInt(fortune + 2) - 2)
                    it.amount *= multiplier
                }
            }

            items.forEach { location.world.dropItemNaturally(location, it) }
        }

        location.world.spawnParticle(
            Particle.ITEM,
            location.x,
            location.y,
            location.z,
            50,
            0.2,
            0.2,
            0.2,
            0.1,
            template.item("")
        )

        template.block?.sounds?.`break`.let {
            location.playSoundAt(
                it?.sound ?: "block.wood.break",
                SoundCategory.BLOCKS,
                it?.volume ?: 1f,
                it?.pitch ?: 1f
            )
        }

        return true
    }
}
