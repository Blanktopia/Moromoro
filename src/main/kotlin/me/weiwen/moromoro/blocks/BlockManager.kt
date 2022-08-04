package me.weiwen.moromoro.managers

import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.blocks.BlockListener
import me.weiwen.moromoro.blocks.BlockTemplate
import me.weiwen.moromoro.blocks.CustomBlock
import me.weiwen.moromoro.blocks.MushroomBlockTemplate
import me.weiwen.moromoro.extensions.playSoundAt
import me.weiwen.moromoro.extensions.sendBlockDamage
import me.weiwen.moromoro.items.ItemManager
import me.weiwen.moromoro.items.item
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.SoundCategory
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.BlockState
import org.bukkit.block.data.MultipleFacing
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

data class DigState(
    val customBlock: CustomBlock,
    val blockFace: BlockFace,
    val breakDuration: Int,
    var ticks: Int,
    val taskId: Int
)

class BlockManager(val plugin: Moromoro, val itemManager: ItemManager) {
    var blockTemplates: MutableMap<String, BlockTemplate> = mutableMapOf()
        private set
    var brownMushroomStates: MutableMap<Int, String> = mutableMapOf()
        private set
    var redMushroomStates: MutableMap<Int, String> = mutableMapOf()
        private set
    var mushroomStemStates: MutableMap<Int, String> = mutableMapOf()
        private set

    var playersDigging: MutableMap<UUID, DigState> = mutableMapOf()

    fun enable() {
        plugin.server.pluginManager.registerEvents(BlockListener(plugin, this, itemManager), plugin)
        load()
    }

    fun disable() {}

    fun load() {
        itemManager
            .templates
            .filterValues { it.block != null }
            .forEach { (key, item) -> register(key, item.block as BlockTemplate) }
    }

    fun register(key: String, blockTemplate: BlockTemplate) {
        blockTemplates[key] = blockTemplate

        if (blockTemplate is MushroomBlockTemplate) {
            val material = blockTemplate.material
            val state = blockTemplate.state
            if (material.isRestrictedCustomBlockState(state)) {
                plugin.logger.warning("Restricted mushroom block state (${state}) used! This is used in vanilla Minecraft and will generate in the world.")
            }
            when (blockTemplate.material) {
                Material.BROWN_MUSHROOM_BLOCK -> brownMushroomStates
                Material.RED_MUSHROOM_BLOCK -> redMushroomStates
                Material.MUSHROOM_STEM -> mushroomStemStates
                else -> {
                    plugin.logger.warning("Expected 'material' matching one of: BROWN_MUSHROOM_BLOCK, RED_MUSHROOM_BLOCk, MUSHROOM_STEM. Found '${blockTemplate.material}' instead.")
                    null
                }
            }?.put(state, key)
        }
    }

    fun startDigging(player: Player, customBlock: CustomBlock, blockFace: BlockFace) {
        if (player.gameMode == GameMode.CREATIVE) {
            customBlock.breakNaturally(player.inventory.itemInMainHand, false)
            return
        }

        if (playersDigging.containsKey(player.uniqueId)) {
            return
        }
        playersDigging[player.uniqueId] = DigState(
            customBlock, blockFace, customBlock.breakDuration(player.inventory.itemInMainHand).toInt(), 0,
            plugin.server.scheduler.scheduleSyncRepeatingTask(plugin, {
                tickDigging(player)
            }, 0L, plugin.config.renderInterval.toLong())
        )
    }

    fun cancelDigging(player: Player) {
        playersDigging[player.uniqueId]?.let {
            player.removePotionEffect(PotionEffectType.SLOW_DIGGING)
            it.customBlock.block.sendBlockDamage(0f, -player.entityId)
            plugin.server.scheduler.cancelTask(it.taskId)
        }
        playersDigging.remove(player.uniqueId)
    }

    fun finishDigging(player: Player) {

    }

    private fun tickDigging(player: Player) {
        playersDigging[player.uniqueId]?.let { digState ->
            player.addPotionEffect(
                PotionEffect(
                    PotionEffectType.SLOW_DIGGING,
                    plugin.config.renderInterval + 20,
                    100,
                    true,
                    false,
                    false
                )
            )
            digState.ticks += plugin.config.renderInterval
            if (digState.ticks >= digState.breakDuration) {
                digState.customBlock.breakNaturally(
                    player.inventory.itemInMainHand,
                    player.gameMode != GameMode.CREATIVE
                )
                cancelDigging(player)
                plugin.server.scheduler.cancelTask(digState.taskId)
            } else {
                if (player.rayTraceBlocks(5.0)?.hitBlock != digState.customBlock.block) {
                    cancelDigging(player)
                    return
                }
                digState.customBlock.block.sendBlockDamage(
                    digState.ticks / digState.breakDuration.toFloat(),
                    -player.entityId
                )
                val template = digState.customBlock.template
                if (template != null) {
                    val location =
                        digState.customBlock.block.location.add(0.5, 0.5, 0.5)
                            .add(digState.blockFace.direction.multiply(0.5))
                    digState.customBlock.block.world.spawnParticle(
                        Particle.ITEM_CRACK,
                        location.x,
                        location.y,
                        location.z,
                        5,
                        0.2 - digState.blockFace.modX * 0.1,
                        0.2 - digState.blockFace.modY * 0.1,
                        0.2 - digState.blockFace.modZ * 0.1,
                        0.05,
                        template.item("")
                    )
                    template.block?.sounds?.hit.let {
                        location.block.playSoundAt(
                            it?.sound ?: "block.wood.hit",
                            SoundCategory.BLOCKS,
                            it?.volume ?: 0.25f,
                            it?.pitch ?: 1f
                        )
                    }
                }
            }
        }
    }
}

var BlockState.customBlockState: Int?
    get() {
        val multipleFacing = blockData as? MultipleFacing ?: return null
        var state = 0
        for (face in multipleFacing.faces) {
            when (face) {
                BlockFace.DOWN -> state += 1
                BlockFace.EAST -> state += 2
                BlockFace.NORTH -> state += 4
                BlockFace.SOUTH -> state += 8
                BlockFace.UP -> state += 16
                BlockFace.WEST -> state += 32
            }
        }
        return state
    }
    set(state: Int?) {
        val multipleFacing = blockData as? MultipleFacing ?: return
        var state = state ?: return
        if (state >= 32) {
            multipleFacing.setFace(BlockFace.WEST, true)
            state -= 32
        } else {
            multipleFacing.setFace(BlockFace.WEST, false)
        }

        if (state >= 16) {
            multipleFacing.setFace(BlockFace.UP, true)
            state -= 16
        } else {
            multipleFacing.setFace(BlockFace.UP, false)
        }

        if (state >= 8) {
            multipleFacing.setFace(BlockFace.SOUTH, true)
            state -= 8
        } else {
            multipleFacing.setFace(BlockFace.SOUTH, false)
        }

        if (state >= 4) {
            multipleFacing.setFace(BlockFace.NORTH, true)
            state -= 4
        } else {
            multipleFacing.setFace(BlockFace.NORTH, false)
        }

        if (state >= 2) {
            multipleFacing.setFace(BlockFace.EAST, true)
            state -= 2
        } else {
            multipleFacing.setFace(BlockFace.EAST, false)
        }

        if (state >= 1) {
            multipleFacing.setFace(BlockFace.DOWN, true)
            state -= 1
        } else {
            multipleFacing.setFace(BlockFace.DOWN, false)
        }

        setBlockData(multipleFacing)
    }

val Block.isCustomBlock: Boolean
    get() {
        return type == Material.BROWN_MUSHROOM_BLOCK
                || type == Material.RED_MUSHROOM_BLOCK
                || type == Material.MUSHROOM_STEM
    }

var Block.customBlockState: Int?
    get() {
        if (type != Material.BROWN_MUSHROOM_BLOCK
            && type != Material.RED_MUSHROOM_BLOCK
            && type != Material.MUSHROOM_STEM
        ) {
            return null
        }

        val multipleFacing = blockData as? MultipleFacing ?: return null
        var state = 0
        for (face in multipleFacing.faces) {
            when (face) {
                BlockFace.DOWN -> state += 1
                BlockFace.EAST -> state += 2
                BlockFace.NORTH -> state += 4
                BlockFace.SOUTH -> state += 8
                BlockFace.UP -> state += 16
                BlockFace.WEST -> state += 32
            }
        }

        if (type.isRestrictedCustomBlockState(state)) {
            return null
        }

        return state
    }
    set(state: Int?) {
        if (type != Material.BROWN_MUSHROOM_BLOCK
            && type != Material.RED_MUSHROOM_BLOCK
            && type != Material.MUSHROOM_STEM
        ) {
            return
        }
        val multipleFacing = blockData as? MultipleFacing ?: return
        var state = state ?: return
        if (state >= 32) {
            multipleFacing.setFace(BlockFace.WEST, true)
            state -= 32
        } else {
            multipleFacing.setFace(BlockFace.WEST, false)
        }

        if (state >= 16) {
            multipleFacing.setFace(BlockFace.UP, true)
            state -= 16
        } else {
            multipleFacing.setFace(BlockFace.UP, false)
        }

        if (state >= 8) {
            multipleFacing.setFace(BlockFace.SOUTH, true)
            state -= 8
        } else {
            multipleFacing.setFace(BlockFace.SOUTH, false)
        }

        if (state >= 4) {
            multipleFacing.setFace(BlockFace.NORTH, true)
            state -= 4
        } else {
            multipleFacing.setFace(BlockFace.NORTH, false)
        }

        if (state >= 2) {
            multipleFacing.setFace(BlockFace.EAST, true)
            state -= 2
        } else {
            multipleFacing.setFace(BlockFace.EAST, false)
        }

        if (state >= 1) {
            multipleFacing.setFace(BlockFace.DOWN, true)
            state -= 1
        } else {
            multipleFacing.setFace(BlockFace.DOWN, false)
        }

        setBlockData(multipleFacing, false)
    }

fun Material.isRestrictedCustomBlockState(state: Int): Boolean {
    return when (this) {
        Material.BROWN_MUSHROOM_BLOCK -> setOf(
            0b000000,
            0b010000,
            0b010010,
            0b010100,
            0b010110,
            0b011000,
            0b011010,
            0b110000,
            0b110100,
            0b111000,
            0b111111,
        )
        Material.RED_MUSHROOM_BLOCK -> setOf(
            0b000000,
            0b000010,
            0b000100,
            0b000110,
            0b001000,
            0b001010,
            0b010000,
            0b010010,
            0b010100,
            0b010110,
            0b011000,
            0b011010,
            0b100000,
            0b100100,
            0b101000,
            0b110000,
            0b110100,
            0b111000,
            0b111111,
        )
        Material.MUSHROOM_STEM -> setOf(
            0b000000,
            0b101110,
            0b111111,
        )
        else -> null
    }?.contains(state) ?: false
}