package me.weiwen.moromoro.actions.mechanic.blockzapper

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.extensions.*
import me.weiwen.moromoro.managers.isCustomBlock
import org.bukkit.*
import org.bukkit.block.BlockFace
import org.bukkit.block.data.*
import org.bukkit.block.data.type.Leaves
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

        replaceBlock(ctx, material)

        return true
    }

    private fun replaceBlock(ctx: Context, material: Material): Boolean {
        val player = ctx.player ?: return false
        val baseBlock = ctx.block ?: return false
        val blockFace = ctx.blockFace ?: return false

        val (block, placedAgainst) = if (player.isSneaking) {
            Pair(baseBlock.getRelative(blockFace), baseBlock)
        } else {
            Pair(baseBlock, blockFace.let { baseBlock.getRelative(it.oppositeFace) })
        }

        val state = block.state
        state.type = material

        val cost = ItemStack(material, 1)
        if (player.gameMode != GameMode.CREATIVE) {
            if (!player.hasAtLeastInInventoryOrShulkerBoxes(cost)) {
                player.playSoundTo(Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, SoundCategory.PLAYERS, 1.0f, 1.0f)
                if (block.type.isItem) {
                    val name = ItemStack(material).i18NDisplayName
                    player.sendActionBar("${ChatColor.RED}Not enough ${name}.")
                }
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
            val couldntRemove = player.removeItemFromInventoryOrShulkerBoxes(cost)
            if (couldntRemove != null) {
                player.playSoundTo(Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, SoundCategory.PLAYERS, 1.0f, 1.0f)
                if (block.type.isItem) {
                    val name = ItemStack(material).i18NDisplayName
                    player.sendActionBar("${ChatColor.RED}Not enough ${name}.")
                }
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
            if (this is Leaves) (blockData as? Leaves)?.let { isPersistent = true }
            if (this is Orientable) (blockData as? Orientable)?.let { axis = it.axis }
            if (this is Directional) (blockData as? Directional)?.let { facing = it.facing }
            if (this is MultipleFacing) (blockData as? MultipleFacing)?.let {
                it.faces.forEach { face -> setFace(face, true) }
            }
            if (this is Wall) (blockData as? Wall)?.let {
                listOf(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST).forEach { face ->
                    setHeight(face, it.getHeight(face))
                }
            }
            if (this is Openable) (blockData as? Openable)?.let { isOpen = it.isOpen }
            if (this is Rotatable) (blockData as? Rotatable)?.let { rotation = it.rotation }
            if (this is Bisected) (blockData as? Bisected)?.let { half = half }
            if (this is Slab) (blockData as? Slab)?.let {
                if (it.type == Slab.Type.DOUBLE) return@apply
                type = it.type
            }
        }

        state.update(true, false)

        player.location.spawnParticleLine(block.location.add(0.5, 0.5, 0.5), Particle.BLOCK_DUST, 0.2, material.createBlockData())
        block.playSoundAt(block.soundGroup.placeSound, SoundCategory.BLOCKS, 1.0f, 1.0f)

        return true
    }
}

