@file:UseSerializers(MaterialSerializer::class)

package me.weiwen.moromoro.actions.mechanic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.extensions.canBuildAt
import me.weiwen.moromoro.extensions.playSoundAt
import me.weiwen.moromoro.serializers.MaterialSerializer
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.SoundCategory

@Serializable
@SerialName("sponge")
data class Sponge(val range: Int = 0) : Action {
    override fun perform(ctx: Context): Boolean {
        val location = ctx.player?.eyeLocation ?: return false

        val player = ctx.player ?: return false

        var didRemove = false
        for (x in -range..range) {
            for (y in -range..range) {
                for (z in -range..range) {
                    val loc = location.clone().add(x.toDouble(), y.toDouble(), z.toDouble())

                    if (!player.canBuildAt(loc)) continue

                    val other = loc.block

                    if (other.type != Material.WATER || other.type != Material.LAVA) continue

                    other.type = Material.AIR


                    didRemove = true

                }
            }
        }

        if (didRemove) {
            player.playSoundAt(Sound.BLOCK_GRASS_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f)
        }

        return didRemove
    }
}
