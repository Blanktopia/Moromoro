package me.weiwen.moromoro.types

import org.bukkit.inventory.EquipmentSlot

enum class CustomEquipmentSlot {
    HAND, OFF_HAND, FEET, LEGS, CHEST, HEAD, TRINKET
}

val CustomEquipmentSlot.equipmentSlot: EquipmentSlot?
    get() = when (this) {
        CustomEquipmentSlot.HAND -> EquipmentSlot.HAND
        CustomEquipmentSlot.OFF_HAND -> EquipmentSlot.OFF_HAND
        CustomEquipmentSlot.FEET -> EquipmentSlot.FEET
        CustomEquipmentSlot.LEGS -> EquipmentSlot.LEGS
        CustomEquipmentSlot.CHEST -> EquipmentSlot.CHEST
        CustomEquipmentSlot.HEAD -> EquipmentSlot.HEAD
        else -> null
    }

val EquipmentSlot.customEquipmentSlot: CustomEquipmentSlot
    get() = when (this) {
        EquipmentSlot.HAND -> CustomEquipmentSlot.HAND
        EquipmentSlot.OFF_HAND -> CustomEquipmentSlot.OFF_HAND
        EquipmentSlot.FEET -> CustomEquipmentSlot.FEET
        EquipmentSlot.LEGS -> CustomEquipmentSlot.LEGS
        EquipmentSlot.CHEST -> CustomEquipmentSlot.CHEST
        EquipmentSlot.HEAD -> CustomEquipmentSlot.HEAD
    }
