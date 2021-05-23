package me.weiwen.moromoro.extensions

import org.bukkit.Location
import org.bukkit.Rotation
import kotlin.math.roundToInt

val Location.rotation: Rotation
get() =
    when (Math.floorMod((yaw / 45).roundToInt(), 8)) {
        0 -> Rotation.FLIPPED
        1 -> Rotation.FLIPPED_45
        2 -> Rotation.COUNTER_CLOCKWISE
        3 -> Rotation.COUNTER_CLOCKWISE_45
        4 -> Rotation.NONE
        5 -> Rotation.CLOCKWISE_45
        6 -> Rotation.CLOCKWISE
        7 -> Rotation.CLOCKWISE_135
        else -> Rotation.NONE
    }
