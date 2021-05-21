package me.weiwen.moromoro.extensions

import org.bukkit.util.Vector
import kotlin.math.asin
import kotlin.math.atan2

var Vector.pitch: Double
    get() {
        val normalized = clone().normalize()
        return asin(normalized.y)
    }
    set(value: Double) {
        val length = length()
        val yaw = yaw
        setX(0)
        setY(0)
        setZ(1)
        rotateAroundX(-value)
        rotateAroundY(-yaw)
        multiply(length)
    }

var Vector.yaw: Double
    get() {
        val normalized = clone().normalize()
        return -atan2(normalized.x, normalized.z)
    }
    set(value: Double) {
        val length = length()
        val pitch = pitch
        setX(0)
        setY(0)
        setZ(1)
        rotateAroundX(-pitch)
        rotateAroundY(-value)
        multiply(length)
    }
