package me.weiwen.moromoro.actions

import me.weiwen.moromoro.extensions.canMineBlock
import org.bukkit.util.Vector

class HammerBlockAction(radius: Int?, depth: Int?) : Action {
    private val radius = (radius ?: 1) - 1
    private val depth = (depth ?: 1) - 1

    override fun perform(ctx: Context): Boolean {
        val block = ctx.block ?: return false
        val player = ctx.player
        val blockFace = ctx.blockFace ?: player.rayTraceBlocks(6.0)?.hitBlockFace ?: return false
        val item = ctx.item

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

        for (x in -radius .. radius) {
            for (y in -radius..radius) {
                for (z in 0..depth) {
                    val loc = block.location.clone()
                        .add(xOffset.clone().multiply(x))
                        .add(yOffset.clone().multiply(y))
                        .add(zOffset.clone().multiply(z))
//                    if (!player.canBuildAt(loc)) continue
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

