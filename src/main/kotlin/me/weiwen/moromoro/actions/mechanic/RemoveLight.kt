@file:UseSerializers(MaterialSerializer::class)

package me.weiwen.moromoro.actions.mechanic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.extensions.canBuildAt
import me.weiwen.moromoro.extensions.playSoundAt
import me.weiwen.moromoro.extensions.spawnParticle
import me.weiwen.moromoro.serializers.MaterialSerializer
import org.bukkit.*

@Serializable
@SerialName("remove-light")
data class RemoveLight(val range: Int = 0) : Action {
    override fun perform(ctx: Context): Boolean {
        val face = ctx.blockFace ?: return false
        val block = ctx.block?.getRelative(face) ?: return false

        val player = ctx.player ?: return false

        var didRemove = false
        for (x in -range..range) {
            for (y in -range..range) {
                for (z in -range..range) {
                    val loc = block.location.clone().add(x.toDouble(), y.toDouble(), z.toDouble())

                    if (!player.canBuildAt(loc)) continue

                    val other = loc.block

                    if (other.type != Material.LIGHT) continue

                    other.type = Material.AIR

                    other.spawnParticle(Particle.ASH, 1, 0.0)
                    other.playSoundAt(Sound.BLOCK_CANDLE_EXTINGUISH, SoundCategory.BLOCKS, 1.0f, 1.0f)
                    other.world.spawnParticle(
                        Particle.BLOCK_MARKER,
                        other.x + 0.5,
                        other.y + 0.5,
                        other.z + 0.5,
                        1,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        Bukkit.getServer().createBlockData(Material.LIGHT)
                    )
                    other.world.spawnParticle(
                        Particle.BLOCK_MARKER,
                        other.x + 0.5,
                        other.y + 0.5,
                        other.z + 0.5,
                        1,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        Bukkit.getServer().createBlockData(Material.BARRIER)
                    )

                    didRemove = true

                }
            }
        }

        return didRemove
    }
}
