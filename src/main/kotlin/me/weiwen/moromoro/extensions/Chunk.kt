package me.weiwen.moromoro.extensions

//import net.minecraft.network.protocol.game.PacketPlayOutMapChunk
//import net.minecraft.network.protocol.game.PacketPlayOutUnloadChunk
import org.bukkit.Chunk
//import org.bukkit.craftbukkit.v1_18_R1.CraftChunk
//import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer
import org.bukkit.entity.Player

fun Chunk.send(player: Player) {
//    val conn = (player as CraftPlayer).handle.b
//    conn.sendPacket(PacketPlayOutUnloadChunk(this.x, this.z))
//    conn.sendPacket(PacketPlayOutMapChunk((this as CraftChunk).handle))
}
