package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.extensions.canBuildAt
import me.weiwen.moromoro.extensions.isPartiallyEmpty
import org.bukkit.Bukkit
import org.bukkit.FluidCollisionMode
import org.bukkit.Material
import org.bukkit.block.data.Levelled

@Serializable
@SerialName("lava-bucket")
object LavaBucket : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player

        val result = player.rayTraceBlocks(5.0, FluidCollisionMode.SOURCE_ONLY) ?: return false
        val block = result.hitBlock ?: return false

        if (!player.canBuildAt(block.location)) {
            return false
        }

        val state = block.state

        if (block.type == Material.WATER || block.type == Material.LAVA) {
            state.type = Material.AIR
            state.update(true)
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

            state.type = Material.LAVA

            val data = Bukkit.getServer().createBlockData(Material.LAVA)
            (data as? Levelled)?.level = 0
            state.blockData = data

            state.update(true)

            return true
        }
    }
}

