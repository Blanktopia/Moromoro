/* Taken from https://github.com/ArtFect/BlockHighlight/blob/master/src/main/java/ru/fiw/blockhighlight/PacketUtil.java */

package me.weiwen.moromoro.extensions

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.utility.MinecraftReflection
import com.comphenix.protocol.wrappers.MinecraftKey
import org.bukkit.Location
import org.bukkit.entity.Player
import java.lang.reflect.InvocationTargetException

fun Player.highlightBlock(location: Location, color: Int, duration: Int) {
//    val packet = Unpooled.buffer()
//    packet.writeLong(locationToLong(location))
//    packet.writeInt(color)
//    packet.writeInt(duration)
//    sendPayload(this, "debug/game_test_add_marker", packet)
}

fun Player.clearHighlights() {
//    sendPayload(this, "debug/game_test_clear", Unpooled.wrappedBuffer(ByteArray(0)))
}

//fun sendPayload(player: Player, channel: String, bytes: ByteBuf) {
//    val handle = PacketContainer(PacketType.Play.Server.CUSTOM_PAYLOAD)
//    handle.minecraftKeys.write(0, MinecraftKey(channel))
//
//    val serializer = MinecraftReflection.getPacketDataSerializer(bytes)
//    handle.modifier.withType<Any>(ByteBuf::class.java).write(0, serializer)
//
//    try {
//        ProtocolLibrary.getProtocolManager().sendServerPacket(player, handle)
//    } catch (e: InvocationTargetException) {
//        throw RuntimeException("Unable to send the packet", e)
//    }
//}
//
//fun locationToLong(location: Location): Long {
//    val (x, y, z) = location
//    return (x.toLong() and 67108863L) shl 38 or y.toLong() and 4095L or (z.toLong() and 67108863L) shl 12
//}