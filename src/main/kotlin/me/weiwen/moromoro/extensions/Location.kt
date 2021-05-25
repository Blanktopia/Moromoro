package me.weiwen.moromoro.extensions

import org.bukkit.Location
import org.bukkit.Rotation
import kotlin.math.roundToInt

var Location.rotation: Rotation
get() =
    when (Math.floorMod((yaw / 45).roundToInt(), 8)) {
        0 -> Rotation.NONE
        1 -> Rotation.CLOCKWISE_45
        2 -> Rotation.CLOCKWISE
        3 -> Rotation.CLOCKWISE_135
        4 -> Rotation.FLIPPED
        5 -> Rotation.FLIPPED_45
        6 -> Rotation.COUNTER_CLOCKWISE
        7 -> Rotation.COUNTER_CLOCKWISE_45
        else -> Rotation.NONE
    }
set(rotation) {
    yaw = when (rotation) {
        Rotation.NONE -> 0f
        Rotation.CLOCKWISE_45 -> 45f
        Rotation.CLOCKWISE -> 90f
        Rotation.CLOCKWISE_135 -> 135f
        Rotation.FLIPPED -> 180f
        Rotation.FLIPPED_45 -> 225f
        Rotation.COUNTER_CLOCKWISE -> 270f
        Rotation.COUNTER_CLOCKWISE_45 -> 315f
    }
}