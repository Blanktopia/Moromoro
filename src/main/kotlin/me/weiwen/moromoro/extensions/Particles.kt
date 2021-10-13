package me.weiwen.moromoro.extensions

import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.block.Block
import org.bukkit.entity.Entity

fun Location.spawnParticle(particle: Particle, count: Int, speed: Double) {
    world.spawnParticle(particle, this, count, 0.4, 0.4, 0.4, speed)
}

fun Location.spawnParticleLine(to: Location, particle: Particle, count: Int, interval: Double) {
    val vec = to.toVector().subtract(this.toVector())
    val offset = vec.clone().normalize().multiply(interval)
    val total = (vec.length() / offset.length()).toInt()
    (0..total).forEach { i ->
        world.spawnParticle(particle, this.clone().add(offset.clone().multiply(i)), count, 0.0, 0.0, 0.0, 0.0)
    }
}

fun Block.spawnParticle(particle: Particle, count: Int, speed: Double) {
    world.spawnParticle(particle, x + 0.5, y + 0.5, z + 0.6, count, 0.4, 0.4, 0.4, speed)
}

fun Block.spawnParticleLine(to: Block, particle: Particle, count: Int, interval: Double) {
    location.add(0.5, 0.5, 0.5).spawnParticleLine(to.location.add(0.5, 0.5, 0.5), particle, count, interval)
}

fun Entity.spawnParticle(particle: Particle, count: Int, speed: Double) {
    world.spawnParticle(particle, location.x, location.y + height/2, location.z, count, 0.3, height/2, 0.0, speed)
}