package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.extensions.canBuildAt
import me.weiwen.moromoro.extensions.isPartiallyEmpty
import me.weiwen.moromoro.extensions.playSoundAt
import org.bukkit.*
import org.bukkit.block.data.Levelled
import org.bukkit.block.data.Waterlogged

@Serializable
@SerialName("lava-bucket")
object LavaBucket : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false

        var block = ctx.block

        if (block != null) {
            if (!player.canBuildAt(block.location)) {
                return false
            }

            val state = block.state

            if (block.type == Material.LAVA_CAULDRON) {
                state.type = Material.CAULDRON

                state.update(true)

                block.playSoundAt(Sound.ITEM_BUCKET_FILL_LAVA, SoundCategory.BLOCKS, 1.0f, 1.0f)
                return true

            } else if (block.type == Material.CAULDRON) {
                state.type = Material.LAVA_CAULDRON

                val data = state.blockData
                (data as? Levelled)?.level = 3
                state.blockData = data

                state.update(true)

                block.playSoundAt(Sound.ITEM_BUCKET_EMPTY_LAVA, SoundCategory.BLOCKS, 1.0f, 1.0f)
                return true
            }
        }

        val result = player.rayTraceBlocks(5.0, FluidCollisionMode.SOURCE_ONLY) ?: return false
        block = result.hitBlock ?: return false

        if (block.type == Material.WATER || block.type == Material.LAVA) {
            val state = block.state

            state.type = Material.AIR

            state.update(true)

            block.playSoundAt(Sound.ITEM_BUCKET_EMPTY_LAVA, SoundCategory.BLOCKS, 1.0f, 1.0f)
            return true

        } else {
            val face = result.hitBlockFace ?: return false
            val block = block.getRelative(face)

            if (!player.canBuildAt(block.location)) {
                return false
            }

            if (!player.canBuildAt(block.location)) {
                return false
            }

            if (!block.type.isPartiallyEmpty) {
                return false
            }

            val state = block.state

            state.type = Material.WATER

            val data = Bukkit.getServer().createBlockData(Material.LAVA)
            (data as? Levelled)?.level = 0
            state.blockData = data

            state.update(true)

            block.playSoundAt(Sound.ITEM_BUCKET_EMPTY_LAVA, SoundCategory.BLOCKS, 1.0f, 1.0f)

            return true
        }
    }
}

