package me.weiwen.moromoro.actions.mechanic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.extensions.canBuildAt
import me.weiwen.moromoro.extensions.isPartiallyEmpty
import me.weiwen.moromoro.extensions.playSoundAt
import org.bukkit.*
import org.bukkit.block.data.Levelled
import org.bukkit.block.data.Waterlogged

@Serializable
@SerialName("water-bucket")
object WaterBucket : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false

        if (player.world.environment == World.Environment.NETHER) return false

        var block = ctx.block

        if (block != null) {
            if (!player.canBuildAt(block.location)) {
                return false
            }

            val state = block.state

            if (block.type == Material.WATER_CAULDRON) {
                state.type = Material.CAULDRON

                state.update(true)

                block.playSoundAt(Sound.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f)
                return true

            } else if (block.type == Material.CAULDRON) {
                state.type = Material.WATER_CAULDRON

                val data = state.blockData
                (data as? Levelled)?.level = 3
                state.blockData = data

                state.update(true)

                block.playSoundAt(Sound.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f)
                return true

            } else if (state.blockData is Waterlogged) {
                val data = state.blockData as Waterlogged

                data.isWaterlogged = !data.isWaterlogged

                state.blockData = data

                state.update()

                block.playSoundAt(
                    if (data.isWaterlogged) {
                        Sound.ITEM_BUCKET_FILL
                    } else {
                        Sound.ITEM_BUCKET_EMPTY
                    }, SoundCategory.BLOCKS, 1.0f, 1.0f
                )

                return true
            }
        }

        val result = player.rayTraceBlocks(5.0, FluidCollisionMode.SOURCE_ONLY) ?: return false
        block = result.hitBlock ?: return false

        if (block.type == Material.WATER || block.type == Material.LAVA) {
            val state = block.state

            state.type = Material.AIR

            state.update(true)

            block.playSoundAt(Sound.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f)
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

            val data = Bukkit.getServer().createBlockData(Material.WATER)
            (data as? Levelled)?.level = 0
            state.blockData = data

            state.update(true)

            block.playSoundAt(Sound.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f)

            return true
        }
    }
}

