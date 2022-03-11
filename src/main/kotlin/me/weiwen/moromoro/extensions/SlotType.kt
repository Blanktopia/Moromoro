package me.weiwen.moromoro.extensions

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent.SlotType.*
import org.bukkit.inventory.EquipmentSlot

val PlayerArmorChangeEvent.SlotType.equipmentSlot: EquipmentSlot
    get() = when (this) {
        HEAD -> EquipmentSlot.HEAD
        CHEST -> EquipmentSlot.CHEST
        LEGS -> EquipmentSlot.LEGS
        FEET -> EquipmentSlot.FEET
    }
