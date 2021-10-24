package me.weiwen.moromoro.extensions

import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import kotlin.math.max
import kotlin.math.min

fun Location.spawnParticle(particle: Particle, r: Double, g: Double, b: Double, extra: Double) {
    world.spawnParticle(particle, this, 0, r, g, b, extra)
}

fun Location.spawnParticle(particle: Particle, count: Int, offset: Double, speed: Double) {
    world.spawnParticle(particle, this, count, offset, offset, offset, speed)
}

fun Location.spawnParticle(particle: Particle, count: Int, speed: Double) {
    spawnParticle(particle, count, 0.4, speed)
}

fun Location.spawnParticleLine(to: Location, particle: Particle, offset: Double, speed: Double, count: Int, interval: Double) {
    val vec = to.toVector().subtract(this.toVector())
    val delta = vec.clone().normalize().multiply(interval)
    val total = (vec.length() / delta.length()).toInt()
    (0..total).forEach { i ->
        world.spawnParticle(particle, this.clone().add(delta.clone().multiply(i)), count, offset, offset, offset, speed)
    }
}

fun Location.spawnParticleLine(to: Location, particle: Particle, interval: Double) {
    spawnParticleLine(to, particle, 0.0, 0.0, 1, interval)
}

fun Block.spawnParticleCuboid(to: Block, particle: Particle, interval: Double) {
    val x0 = min(x, to.x).toDouble()
    val x1 = max(x, to.x) + 1.0
    val y0 = min(y, to.y).toDouble()
    val y1 = max(y, to.y) + 1.0
    val z0 = min(z, to.z).toDouble()
    val z1 = max(z, to.z) + 1.0

    val locations = listOf(
        Pair(Location(world, x0, y0, z0), Location(world, x0, y0, z1)),
        Pair(Location(world, x0, y0, z0), Location(world, x0, y1, z0)),
        Pair(Location(world, x0, y0, z0), Location(world, x1, y0, z0)),
        Pair(Location(world, x0, y0, z1), Location(world, x0, y1, z1)),
        Pair(Location(world, x0, y1, z0), Location(world, x0, y1, z1)),
        Pair(Location(world, x0, y1, z0), Location(world, x1, y1, z0)),
        Pair(Location(world, x1, y0, z0), Location(world, x1, y1, z0)),
        Pair(Location(world, x1, y0, z1), Location(world, x0, y0, z1)),
        Pair(Location(world, x1, y0, z1), Location(world, x1, y0, z0)),
        Pair(Location(world, x1, y0, z1), Location(world, x1, y1, z1)),
        Pair(Location(world, x1, y1, z1), Location(world, x0, y1, z1)),
        Pair(Location(world, x1, y1, z1), Location(world, x1, y1, z0)),
    )

    locations.forEach {
        it.first.spawnParticleLine(it.second, particle, interval)
    }
}

fun Block.spawnParticle(particle: Particle, count: Int, speed: Double) {
    world.spawnParticle(particle, x + 0.5, y + 0.5, z + 0.6, count, 0.4, 0.4, 0.4, speed)
}

fun Block.spawnParticleLine(to: Block, particle: Particle, interval: Double) {
    location.add(0.5, 0.5, 0.5).spawnParticleLine(to.location.add(0.5, 0.5, 0.5), particle, interval)
}

fun Entity.spawnParticle(particle: Particle, count: Int, speed: Double) {
    world.spawnParticle(particle, location.x, location.y + height/2, location.z, count, 0.3, height/2, 0.0, speed)
}