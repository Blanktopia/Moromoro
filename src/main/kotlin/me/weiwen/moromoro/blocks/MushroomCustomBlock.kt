package me.weiwen.moromoro.blocks

import me.weiwen.moromoro.managers.BlockManager
import me.weiwen.moromoro.managers.customBlockState
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack

class MushroomCustomBlock(override val block: Block, override val key: String) : CustomBlock {
    companion object {
        fun fromBlock(block: Block): MushroomCustomBlock? {
            val states = when (block.type) {
                Material.BROWN_MUSHROOM_BLOCK -> BlockManager.brownMushroomStates
                Material.RED_MUSHROOM_BLOCK -> BlockManager.redMushroomStates
                Material.MUSHROOM_STEM -> BlockManager.mushroomStemStates
                else -> return null
            }
            val state = block.customBlockState ?: return null
            val key = states[state] ?: return null
            return MushroomCustomBlock(block, key)
        }
    }

    override fun breakNaturally(tool: ItemStack?, dropItem: Boolean, location: Location?): Boolean {
        val ret = super.breakNaturally(tool, dropItem, block.location)
        if (ret) {
            block.setType(Material.AIR, true)
        }
        return ret
    }
}