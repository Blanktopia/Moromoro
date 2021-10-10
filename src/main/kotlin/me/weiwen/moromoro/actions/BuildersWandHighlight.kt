@file:UseSerializers(MaterialSerializer::class)

package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.extensions.highlightBlock
import me.weiwen.moromoro.extensions.isPartial
import me.weiwen.moromoro.extensions.isPartiallyEmpty
import me.weiwen.moromoro.extensions.isShulker
import me.weiwen.moromoro.managers.isCustomBlock
import me.weiwen.moromoro.serializers.MaterialSerializer
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.util.Vector

@Serializable
@SerialName("builders-wand-highlight")
data class BuildersWandHighlight(val range: Int = 1, val color: Long, val duration: Int = 250) : Action {
    private val colorUint = color.toUInt()

    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        val block = ctx.block ?: return false
        val face = ctx.blockFace ?: return false

        if (block.type.isPartial || block.type.isShulker || block.isCustomBlock) {
            return false
        }

        val locations = buildersWandLocations(block, face, range)

        locations.forEach { (_, location) ->
            player.highlightBlock(location, colorUint, duration)
        }

        return true
    }

    private fun buildersWandLocations(block: Block, face: BlockFace, range: Int): MutableList<Pair<Block, Location>> {
        val material = block.type
        val locations: MutableList<Pair<Block, Location>> = mutableListOf()
        for (base in locationsInRange(block.location, face, range)) {
            if (base.block.type != material) continue
            val other = base.clone().add(face.direction)
            if (!other.block.type.isPartiallyEmpty) continue
            locations.add(Pair(base.block, other))
        }
        return locations
    }

    private fun locationsInRange(origin: Location, face: BlockFace, range: Int): MutableList<Location> {
        val (xOffset, yOffset) = if (face.modX != 0) {
            Pair(Vector(0, 1, 0), Vector(0, 0, 1))
        } else if (face.modY != 0) {
            Pair(Vector(1, 0, 0), Vector(0, 0, 1))
        } else {
            Pair(Vector(1, 0, 0), Vector(0, 1, 0))
        }
        val locations: MutableList<Location> = mutableListOf()
        for (x in -range..range) {
            for (y in -range..range) {
                locations.add(
                    origin.clone()
                        .add(xOffset.clone().multiply(x))
                        .add(yOffset.clone().multiply(y))
                )
            }
        }
        return locations
    }

}
