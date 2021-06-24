package me.weiwen.moromoro.extensions

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent.SlotType.*
import net.minecraft.network.protocol.game.PacketPlayOutMapChunk
import net.minecraft.network.protocol.game.PacketPlayOutUnloadChunk
import org.bukkit.Chunk
import org.bukkit.craftbukkit.v1_17_R1.CraftChunk
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot

val PlayerArmorChangeEvent.SlotType.equipmentSlot: EquipmentSlot
    get() = when (this) {
        HEAD -> EquipmentSlot.HEAD
        CHEST -> EquipmentSlot.CHEST
        LEGS -> EquipmentSlot.LEGS
        FEET -> EquipmentSlot.FEET
    }
