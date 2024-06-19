@file:UseSerializers(MaterialSerializer::class)

package me.weiwen.moromoro.actions.mechanic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.serializers.MaterialSerializer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.EquipmentSlot

@Serializable
@SerialName("light")
object Light : Action {
    override fun perform(ctx: Context): Boolean {
        val item = ctx.item ?: return false
        val player = ctx.player ?: return false
        val block = ctx.block ?: return false
        val face = ctx.blockFace ?: return false

        val replacedBlock = block.getRelative(face)

        val material = if (replacedBlock.type == Material.LIGHT) {
            Material.AIR
        } else if (replacedBlock.type == Material.AIR || replacedBlock.type == Material.CAVE_AIR) {
            Material.LIGHT
        } else {
            return false
        }

        val state = replacedBlock.state
        state.type = material

        val buildEvent = BlockPlaceEvent(
            replacedBlock,
            state,
            block,
            item,
            player,
            true,
            EquipmentSlot.HAND
        )
        Bukkit.getPluginManager().callEvent(buildEvent)

        if (buildEvent.isCancelled) {
            return false
        }

        state.update(true)

        replacedBlock.world.spawnParticle(
            Particle.BLOCK_MARKER,
            replacedBlock.x + 0.5,
            replacedBlock.y + 0.5,
            replacedBlock.z + 0.5,
            1,
            0.0,
            0.0,
            0.0,
            0.0,
            Bukkit.getServer().createBlockData(Material.LIGHT)
        )

        return true
    }
}
