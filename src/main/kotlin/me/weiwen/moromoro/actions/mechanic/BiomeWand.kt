@file:UseSerializers(BiomeSerializer::class)

package me.weiwen.moromoro.actions.mechanic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.extensions.canBuildAt
import me.weiwen.moromoro.extensions.playSoundTo
import me.weiwen.moromoro.extensions.send
import me.weiwen.moromoro.extensions.spawnParticle
import me.weiwen.moromoro.serializers.BiomeSerializer
import org.bukkit.*
import org.bukkit.block.Biome
import org.bukkit.block.BlockFace

@Serializable
@SerialName("biome-wand")
data class BiomeWand(val biome: Biome, val range: Int = 1) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        val block = player.rayTraceBlocks(5.0, FluidCollisionMode.ALWAYS)?.hitBlock ?: return false
        val x0 = block.location.blockX
        val z0 = block.location.blockZ

        val chunks = mutableSetOf<Chunk>()
        for (x in -range..range) {
            for (z in -range..range) {
                val other = block.world.getHighestBlockAt(x + x0, z + z0)
                if (player.canBuildAt(other.location)) {
                    other.world.setBiome(other.location.blockX, other.location.blockZ, biome)
                    other.getRelative(BlockFace.UP).spawnParticle(Particle.VILLAGER_HAPPY, 2, 0.01)
                    chunks.add(other.location.chunk)
                }
            }
        }

        if (chunks.size > 0) {
            val players = player.world.getNearbyPlayers(player.location, 64.0, 256.0)
            chunks.forEach { chunk -> players.forEach { chunk.send(it) } }
            block.world.playSound(block.location, Sound.BLOCK_GRASS_PLACE, 1.0f, 1.0f)
            return true
        } else {
            player.playSoundTo(Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, SoundCategory.PLAYERS, 1.0f, 1.0f)
            return false
        }
    }
}
