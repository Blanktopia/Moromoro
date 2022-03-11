package me.weiwen.moromoro.extensions

import com.comphenix.protocol.wrappers.BlockPosition
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.packets.WrapperPlayServerBlockBreakAnimation
import org.bukkit.Bukkit
import org.bukkit.block.Block

fun Block.sendBlockDamage(progress: Float, entityId: Int) {
    if (Bukkit.getServer().pluginManager.isPluginEnabled("ProtocolLib")) {
        val packet = WrapperPlayServerBlockBreakAnimation()
        packet.entityID = entityId
        packet.location = BlockPosition(location.blockX, location.blockY, location.blockZ)
        packet.destroyStage = if (progress == 0f) {
            -1
        } else {
            (progress * 10).toInt()
        }

        val distance = Moromoro.plugin.config.renderDistance * Moromoro.plugin.config.renderDistance
        Bukkit.getServer().onlinePlayers.forEach { player ->
            if (player.world == world && player.location.distanceSquared(location) < distance) {
                packet.sendPacket(player)
            }
        }
    }
}
