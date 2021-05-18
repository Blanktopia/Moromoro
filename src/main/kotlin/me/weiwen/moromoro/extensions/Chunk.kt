package me.weiwen.moromoro.extensions

import net.minecraft.server.v1_16_R3.PacketPlayOutMapChunk
import net.minecraft.server.v1_16_R3.PacketPlayOutUnloadChunk
import org.bukkit.Chunk
import org.bukkit.craftbukkit.v1_16_R3.CraftChunk
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer
import org.bukkit.entity.Player

fun Chunk.send(player: Player) {
    val conn = (player as CraftPlayer).handle.playerConnection
    conn.sendPacket(PacketPlayOutUnloadChunk(this.x, this.z))
    conn.sendPacket(PacketPlayOutMapChunk((this as CraftChunk).handle, 65535))
}
