package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.extensions.*
import org.bukkit.*
import org.bukkit.block.Biome
import org.bukkit.block.BlockFace
import org.bukkit.util.Vector

@Serializable
@SerialName("biome-wand")
data class BiomeWand(val biome: Biome, val range: Int = 1) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player
        val block = player.rayTraceBlocks(5.0, FluidCollisionMode.ALWAYS)?.hitBlock ?: return false

        val chunks = mutableSetOf<Chunk>()
        for (x in -range..range) {
            for (z in -range..range) {
                val block = block.world.getHighestBlockAt(x, z)
                if (player.canBuildAt(block.location)) {
                    block.world.setBiome(x, z, biome)
                    block.getRelative(BlockFace.UP).spawnParticle(Particle.VILLAGER_HAPPY, 2, 0.01)
                    chunks.add(block.location.chunk)
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
