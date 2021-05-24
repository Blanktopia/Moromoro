package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.extensions.canBuildAt
import me.weiwen.moromoro.extensions.isPartiallyEmpty
import org.bukkit.Bukkit
import org.bukkit.FluidCollisionMode
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.data.Levelled
import org.bukkit.block.data.Waterlogged

@Serializable
@SerialName("water-bucket")
object WaterBucket : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player

        if (player.world.environment == World.Environment.NETHER) return false

        val result = player.rayTraceBlocks(5.0, FluidCollisionMode.SOURCE_ONLY) ?: return false

        val block = result.hitBlock ?: return false

        if (!player.canBuildAt(block.location)) {
            return false
        }

        val state = block.state
        val data = state.blockData

        if (block.type == Material.WATER || block.type == Material.LAVA) {
            state.type = Material.AIR
            state.update(true)
            return true

        } else if (block.type == Material.CAULDRON) {
            if (data is Levelled) {
                data.level = if (data.level == 0) { 3 } else { 0 }
            }
            state.blockData = data
            state.update(true)
            return true

        } else if (data is Waterlogged) {
            data.isWaterlogged = !data.isWaterlogged
            state.blockData = data
            state.update()
            return true

        } else {
            val face = result.hitBlockFace ?: return false
            val target = block.getRelative(face)

            if (!player.canBuildAt(target.location)) {
                return false
            }

            if (!target.type.isPartiallyEmpty) {
                return false
            }

            val state = target.state

            state.type = Material.WATER

            val data = Bukkit.getServer().createBlockData(Material.WATER)
            (data as? Levelled)?.level = 0
            state.blockData = data

            state.update(true)

            return true
        }
    }
}

