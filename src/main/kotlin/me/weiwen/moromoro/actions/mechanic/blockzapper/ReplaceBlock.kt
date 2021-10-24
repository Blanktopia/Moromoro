package me.weiwen.moromoro.actions.mechanic.blockzapper

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.extensions.canBuildAt
import me.weiwen.moromoro.extensions.isPartial
import me.weiwen.moromoro.extensions.playSoundAt
import me.weiwen.moromoro.extensions.playSoundTo
import me.weiwen.moromoro.managers.isCustomBlock
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.*
import org.bukkit.block.data.type.Slab
import org.bukkit.block.data.type.Wall
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

@Serializable
@SerialName("replace-block")
object ReplaceBlock : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        val block = ctx.block ?: return false
        val placedAgainst = ctx.blockFace?.let { block.getRelative(it.oppositeFace) } ?: return false

        val material = SelectMaterial.materials[player.uniqueId] ?: return false

        if (!material.isBlock) return false

        if (!player.canBuildAt(block.location)) {
            return false
        }

        if (block.isCustomBlock) {
            return false
        }

        if (block.type.isPartial) {
            return false
        }

        replaceBlock(ctx, material, placedAgainst)

        return true
    }

    private fun replaceBlock(ctx: Context, material: Material, placedAgainst: Block): Boolean {
        val block = ctx.block ?: return false
        val player = ctx.player ?: return false

        val state = block.state
        state.type = material

        val cost = ItemStack(material, 1)
        if (player.gameMode != GameMode.CREATIVE) {
            if (!player.inventory.containsAtLeast(cost, 1)) {
                player.playSoundTo(Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, SoundCategory.PLAYERS, 1.0f, 1.0f)
                return false
            }
        }

        val blockData = block.blockData

        val breakEvent = BlockBreakEvent(
            block,
            player
        )
        Bukkit.getPluginManager().callEvent(breakEvent)
        if (breakEvent.isCancelled) return false
        if (breakEvent.isDropItems) {
            val tool = ItemStack(Material.NETHERITE_PICKAXE).apply {
                addEnchant(Enchantment.SILK_TOUCH, 1, false)
            }
            block.breakNaturally(tool, true)
        } else {
            block.type = Material.AIR
        }

        if (player.gameMode != GameMode.CREATIVE) {
            val couldntRemove = player.inventory.removeItem(cost)
            if (couldntRemove.isNotEmpty()) {
                player.playSoundTo(Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, SoundCategory.PLAYERS, 1.0f, 1.0f)
                return false
            }
        }

        val buildEvent = BlockPlaceEvent(
            block,
            state,
            placedAgainst,
            cost,
            player,
            true,
            EquipmentSlot.HAND
        )
        Bukkit.getPluginManager().callEvent(buildEvent)
        if (buildEvent.isCancelled) return false

        state.blockData = state.blockData.apply {
            when (this) {
                is Orientable -> (blockData as? Orientable)?.let { axis = it.axis }
                is Directional -> (blockData as? Directional)?.let { facing = it.facing }
                is MultipleFacing -> (blockData as? MultipleFacing)?.let {
                    allowedFaces.forEach { face ->
                        setFace(face, it.hasFace(face))
                    }
                }
                is Wall -> (blockData as? Wall)?.let {
                    listOf(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST).forEach { face ->
                        setHeight(face, it.getHeight(face))
                    }
                }
                is Rotatable -> (blockData as? Rotatable)?.let {
                    rotation = it.rotation
                }
                is Bisected -> (blockData as? Bisected)?.let {
                    half = half
                }
                is Slab -> (blockData as? Slab)?.let {
                    if (it.type == Slab.Type.DOUBLE) return@apply
                    type = it.type
                }
            }
        }

        state.update(true)

        block.playSoundAt(block.soundGroup.placeSound, SoundCategory.BLOCKS, 1.0f, 1.0f)

        return true
    }
}

