package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.extensions.canBuildAt
import me.weiwen.moromoro.extensions.canMineBlock
import me.weiwen.moromoro.extensions.playSoundAt
import me.weiwen.moromoro.extensions.stripped
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.block.data.Orientable
import org.bukkit.util.Vector

@Serializable
@SerialName("strip-block")
data class StripBlock(val radius: Int = 0, val depth: Int = 0) : Action {
    override fun perform(ctx: Context): Boolean {
        val block = ctx.block ?: return false
        val player = ctx.player ?: return false
        val blockFace = ctx.blockFace ?: player.rayTraceBlocks(6.0)?.hitBlockFace ?: return false

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

        var didStrip = false
        for (x in -radius..radius) {
            for (y in -radius..radius) {
                for (z in 0..depth) {
                    val loc = block.location.clone()
                        .add(xOffset.clone().multiply(x))
                        .add(yOffset.clone().multiply(y))
                        .add(zOffset.clone().multiply(z))

                    if (!player.canBuildAt(loc)) continue

                    val other = loc.block

                    val stripped = other.type.stripped ?: continue

                    didStrip = true

                    val data = other.blockData

                    other.type = stripped

                    val newData = other.blockData
                    if (data is Orientable && newData is Orientable) {
                        newData.axis = data.axis
                        other.blockData = newData
                    }
                }
            }
        }

        if (didStrip) {
            block.playSoundAt(Sound.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0f, 1.0f)
        }

        return didStrip
    }
}
