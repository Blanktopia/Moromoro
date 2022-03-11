package me.weiwen.moromoro.packets

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.BlockPosition
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerDigType

/**
 * PacketWrapper - ProtocolLib wrappers for Minecraft packets
 * Copyright (C) dmulloy2 <http://dmulloy2.net>
 * Copyright (C) Kristian S. Strangeland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


class WrapperPlayClientBlockDig : AbstractPacket {
    constructor() : super(PacketContainer(TYPE), TYPE) {
        handle.modifier.writeDefaults()
    }

    constructor(packet: PacketContainer?) : super(packet, TYPE) {}
    /**
     * Retrieve Location.
     *
     *
     * Notes: block position
     *
     * @return The current Location
     */
    /**
     * Set Location.
     *
     * @param value - new value.
     */
    var location: BlockPosition?
        get() = handle.blockPositionModifier.read(0)
        set(value) {
            handle.blockPositionModifier.write(0, value)
        }
    var direction: EnumWrappers.Direction?
        get() = handle.directions.read(0)
        set(value) {
            handle.directions.write(0, value)
        }
    /**
     * Retrieve Status.
     *
     *
     * Notes: the action the player is taking against the block (see below)
     *
     * @return The current Status
     */
    /**
     * Set Status.
     *
     * @param value - new value.
     */
    var status: PlayerDigType?
        get() = handle.playerDigTypes.read(0)
        set(value) {
            handle.playerDigTypes.write(0, value)
        }

    companion object {
        val TYPE = PacketType.Play.Client.BLOCK_DIG
    }
}