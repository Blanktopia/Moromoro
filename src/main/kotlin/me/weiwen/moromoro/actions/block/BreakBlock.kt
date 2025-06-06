package me.weiwen.moromoro.actions.block

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.blocks.CustomBlock
import me.weiwen.moromoro.extensions.canBuildAt
import me.weiwen.moromoro.extensions.isRightTool
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.util.Vector

@Serializable
@SerialName("break-block")
data class BreakBlock(val radius: Int = 0, val depth: Int = 0) : Action {
    override fun perform(ctx: Context): Boolean {
        val block = ctx.block ?: return false
        val player = ctx.player ?: return false
        val blockFace = ctx.blockFace ?: player.rayTraceBlocks(6.0)?.hitBlockFace ?: return false
        val item = ctx.item ?: return false

        if (!block.isRightTool(item)) return false

        var hardness = block.type.hardness
        if (block.type == Material.DIRT) {
            hardness = Material.GRASS_BLOCK.hardness
        }

        val (xOffset, yOffset, zOffset) = when {
            blockFace.modX != 0 -> {
                Triple(Vector(0, 1, 0), Vector(0, 0, 1), Vector(-blockFace.modX, 0, 0))
            }
            blockFace.modY != 0 -> {
                Triple(Vector(1, 0, 0), Vector(0, 0, 1), Vector(0, -blockFace.modY, 0))
            }
            else -> {
                Triple(Vector(1, 0, 0), Vector(0, 1, 0), Vector(0, 0, -blockFace.modZ))
            }
        }

        for (x in -radius..radius) {
            for (y in -radius..radius) {
                if (x == 0 && y == 0) continue
                for (z in 0..depth) {
                    val loc = block.location.clone()
                        .add(xOffset.clone().multiply(x))
                        .add(yOffset.clone().multiply(y))
                        .add(zOffset.clone().multiply(z))
                    if (!player.canBuildAt(loc)) continue
                    val other = loc.block
                    if (other.type.hardness > hardness) continue
                    if (!other.isRightTool(item)) continue

                    val customBlock = CustomBlock.fromBlock(other)
                    if (customBlock != null) {
                        customBlock.breakNaturally(item, player.gameMode != GameMode.CREATIVE)
                    } else {
                        other.breakNaturally(item)
                    }
                }
            }
        }
        return true
    }
}
