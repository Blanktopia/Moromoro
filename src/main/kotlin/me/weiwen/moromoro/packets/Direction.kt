package me.weiwen.moromoro.packets

import org.bukkit.block.BlockFace
import com.github.retrooper.packetevents.protocol.world.BlockFace as PacketBlockFace

val PacketBlockFace.blockFace: BlockFace?
    get() = when (this) {
        PacketBlockFace.DOWN -> BlockFace.DOWN
        PacketBlockFace.UP -> BlockFace.UP
        PacketBlockFace.NORTH -> BlockFace.NORTH
        PacketBlockFace.SOUTH -> BlockFace.SOUTH
        PacketBlockFace.WEST -> BlockFace.WEST
        PacketBlockFace.EAST -> BlockFace.EAST
        PacketBlockFace.OTHER -> null
    }