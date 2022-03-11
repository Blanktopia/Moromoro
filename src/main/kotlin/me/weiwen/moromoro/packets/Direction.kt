package me.weiwen.moromoro.packets

import com.comphenix.protocol.wrappers.EnumWrappers
import org.bukkit.block.BlockFace

val EnumWrappers.Direction.blockFace: BlockFace
    get() = when (this) {
        EnumWrappers.Direction.DOWN -> BlockFace.DOWN
        EnumWrappers.Direction.UP -> BlockFace.UP
        EnumWrappers.Direction.NORTH -> BlockFace.NORTH
        EnumWrappers.Direction.SOUTH -> BlockFace.SOUTH
        EnumWrappers.Direction.WEST -> BlockFace.WEST
        EnumWrappers.Direction.EAST -> BlockFace.EAST
    }