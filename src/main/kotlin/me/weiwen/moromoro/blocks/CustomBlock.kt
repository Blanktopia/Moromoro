package me.weiwen.moromoro.blocks

import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.extensions.*
import me.weiwen.moromoro.items.ItemTemplate
import me.weiwen.moromoro.items.item
import me.weiwen.moromoro.managers.customBlockState
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.SoundCategory
import org.bukkit.block.Block
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.ItemFrame
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import kotlin.math.ceil
import kotlin.math.max
import kotlin.random.Random

sealed interface CustomBlock {
    companion object {
        fun fromBlock(block: Block): CustomBlock? {
            return MushroomCustomBlock.fromBlock(block) ?: ItemFrameCustomBlock.fromBlock(block)
        }
    }

    val block: Block
    val key: String?

    val template: ItemTemplate?
        get() = Moromoro.plugin.itemManager.templates[key]

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

        val efficiency = when (val level = tool.getEnchantmentLevel(Enchantment.DIG_SPEED)) {
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

    fun breakNaturally(tool: ItemStack?, dropItem: Boolean): Boolean
}

class MushroomCustomBlock(override val block: Block, override val key: String) : CustomBlock {
    companion object {
        fun fromBlock(block: Block): MushroomCustomBlock? {
            val states = when (block.type) {
                Material.BROWN_MUSHROOM_BLOCK -> Moromoro.plugin.blockManager.brownMushroomStates
                Material.RED_MUSHROOM_BLOCK -> Moromoro.plugin.blockManager.redMushroomStates
                Material.MUSHROOM_STEM -> Moromoro.plugin.blockManager.mushroomStemStates
                else -> return null
            }
            val state = block.customBlockState ?: return null
            val key = states[state] ?: return null
            return MushroomCustomBlock(block, key)
        }
    }

    override fun breakNaturally(tool: ItemStack?, dropItem: Boolean): Boolean {
        val template = Moromoro.plugin.blockManager.itemManager.templates[key] ?: return false

        block.setType(Material.AIR, true)

        if (dropItem) {
            val items = if (tool?.enchantments?.get(Enchantment.SILK_TOUCH) == null) {
                val experience = template.block?.experience ?: 0
                if (experience != 0) {
                    (block.world.spawnEntity(
                        block.location.clone().add(0.5, 0.5, 0.5),
                        EntityType.EXPERIENCE_ORB
                    ) as? ExperienceOrb)?.apply {
                        this.experience = experience
                    }
                }
                template.block?.drops?.map { it.clone() } ?: listOf(template.item(key, 1))
            } else {
                listOf(template.item(key, 1))
            }

            if (template.block?.canFortune == true) {
                val fortune = tool?.enchantments?.get(Enchantment.LOOT_BONUS_BLOCKS) ?: 0
                items.forEach {
                    val multiplier = 1 + max(0, Random.nextInt(fortune + 2) - 2)
                    it.amount *= multiplier
                }
            }

            items.forEach { block.world.dropItemNaturally(block.location, it) }
        }

        val location = block.location.add(0.5, 0.5, 0.5)
        block.world.spawnParticle(
            Particle.ITEM_CRACK,
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

        template.block?.sounds?.`break`?.let {
            location.block.playSoundAt(
                it?.sound ?: "block.wood.break",
                SoundCategory.BLOCKS,
                it?.volume ?: 1f,
                it?.pitch ?: 1f

            )
        }

        return true
    }
}

open class ItemFrameCustomBlock(override val block: Block, val itemFrame: ItemFrame, override val key: String) :
    CustomBlock {
    companion object {
        fun fromItemFrame(itemFrame: ItemFrame): ItemFrameCustomBlock? {
            val block = itemFrame.location.block
            val key = itemFrame.persistentDataContainer.get(
                NamespacedKey(Moromoro.plugin.config.namespace, "type"),
                PersistentDataType.STRING
            ) ?: return null

            return if (block.type == Material.BARRIER) {
                ItemFrameBarrierCustomBlock(block, itemFrame, key)
            } else {
                ItemFrameCustomBlock(block, itemFrame, key)
            }
        }

        fun fromBlock(block: Block): ItemFrameCustomBlock? {
            val location = block.location.add(0.5, 0.5, 0.5)

            val itemFrames = location.world.getNearbyEntities(location, 0.5, 0.5, 0.5) {
                it.type == EntityType.ITEM_FRAME &&
                        it.persistentDataContainer.has(
                            NamespacedKey(Moromoro.plugin.config.namespace, "type"),
                            PersistentDataType.STRING
                        )
            }

            if (itemFrames.isEmpty()) {
                return null
            }

            val itemFrame = itemFrames.first() as? ItemFrame ?: return null

            return if (block.type == Material.BARRIER) {
                ItemFrameBarrierCustomBlock(
                    block,
                    itemFrame,
                    itemFrame.persistentDataContainer.get(
                        NamespacedKey(Moromoro.plugin.config.namespace, "type"),
                        PersistentDataType.STRING
                    ) ?: return null
                )
            } else {
                ItemFrameCustomBlock(
                    block,
                    itemFrame,
                    itemFrame.persistentDataContainer.get(
                        NamespacedKey(Moromoro.plugin.config.namespace, "type"),
                        PersistentDataType.STRING
                    ) ?: return null
                )
            }
        }
    }

    override fun breakNaturally(tool: ItemStack?, dropItem: Boolean): Boolean {
        val template = Moromoro.plugin.blockManager.itemManager.templates[key] ?: return false

        if (dropItem) {
            val items = if (tool?.enchantments?.get(Enchantment.SILK_TOUCH) == null) {
                val experience = template.block?.experience ?: 0
                if (experience != 0) {
                    (block.world.spawnEntity(
                        block.location.clone().add(0.5, 0.5, 0.5),
                        EntityType.EXPERIENCE_ORB
                    ) as? ExperienceOrb)?.apply {
                        this.experience = experience
                    }
                }

                template.block?.drops?.map { it.clone() } ?: listOf(template.item(key, 1))
            } else {
                listOf(template.item(key, 1))
            }

            if (template.block?.canFortune == true) {
                val fortune = tool?.enchantments?.get(Enchantment.LOOT_BONUS_BLOCKS) ?: 0
                items.forEach {
                    val multiplier = 1 + max(0, Random.nextInt(fortune + 2) - 2)
                    it.amount *= multiplier
                }
            }

            items.forEach { block.world.dropItemNaturally(block.location, it) }
        }

        itemFrame.remove()

        val location = block.location.add(0.5, 0.5, 0.5)
        block.world.spawnParticle(
            Particle.ITEM_CRACK,
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
            location.block.playSoundAt(
                it?.sound ?: "block.wood.break",
                SoundCategory.BLOCKS,
                it?.volume ?: 1f,
                it?.pitch ?: 1f
            )
        }

        return true
    }
}

class ItemFrameBarrierCustomBlock(block: Block, itemFrame: ItemFrame, key: String) :
    ItemFrameCustomBlock(block, itemFrame, key) {

    override fun breakNaturally(tool: ItemStack?, dropItem: Boolean): Boolean {
        if (super.breakNaturally(tool, dropItem)) {
            block.setType(Material.AIR, true)
            return true
        }
        return false
    }
}
