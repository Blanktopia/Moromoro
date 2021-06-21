package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.extensions.canBuildAt
import me.weiwen.moromoro.extensions.canMineBlock
import org.bukkit.util.Vector

@Serializable
@SerialName("break-block")
data class BreakBlock(val radius: Int = 0, val depth: Int = 0) : Action {
    override fun perform(ctx: Context): Boolean {
        val block = ctx.block ?: return false
        val player = ctx.player ?: return false
        val blockFace = ctx.blockFace ?: player.rayTraceBlocks(6.0)?.hitBlockFace ?: return false
        val item = ctx.item ?: return false

        if (!item.type.canMineBlock(block)) return false

        val hardness = block.type.hardness

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
                    if (!item.type.canMineBlock(other)) continue
                    other.breakNaturally(item)
                }
            }
        }
        return true
    }
}
