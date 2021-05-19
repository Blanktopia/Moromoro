package me.weiwen.moromoro.extensions

import org.bukkit.entity.ExperienceOrb
import kotlin.random.Random

fun ExperienceOrb.setExperience(experience: Double) {
    this.experience = experience.toInt() + if (Random.nextDouble() < experience % 1) { 1 } else { 0 }
}
