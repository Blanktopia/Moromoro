package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Location

@Serializable
sealed class LocationSelector {
    abstract fun center(ctx: Context): Location?
    abstract fun location(ctx: Context): Location?
}

@Serializable
@SerialName("player")
object PlayerLocationSelector : LocationSelector() {
    override fun center(ctx: Context): Location? {
        return ctx.player?.let { it.location.clone().add(0.0, it.height / 2, 0.0) }
    }

    override fun location(ctx: Context): Location? {
        return ctx.player?.let { it.location }
    }
}

@Serializable
@SerialName("entity")
object EntityLocationSelector : LocationSelector() {
    override fun center(ctx: Context): Location? {
        return ctx.entity?.let { it.location.clone().add(0.0, it.height / 2, 0.0) }
    }

    override fun location(ctx: Context): Location? {
        return ctx.entity?.let { it.location }
    }
}

@Serializable
@SerialName("block")
object BlockLocationSelector : LocationSelector() {
    override fun center(ctx: Context): Location? {
        return ctx.block?.let { it.location.clone().add(0.5, 0.5, 0.5) }
    }

    override fun location(ctx: Context): Location? {
        return ctx.block?.let { it.location }
    }
}

@Serializable
@SerialName("projectile")
object ProjectileLocationSelector : LocationSelector() {
    override fun center(ctx: Context): Location? {
        return ctx.projectile?.let { it.location }
    }

    override fun location(ctx: Context): Location? {
        return ctx.projectile?.let { it.location }
    }
}

@Serializable
@SerialName("raycast")
data class RaycastLocationSelector(val range: Double = 5.0) : LocationSelector() {
    override fun center(ctx: Context): Location? {
        return ctx.player?.let { it.rayTraceBlocks(range)?.hitPosition?.toLocation(it.world) }
    }

    override fun location(ctx: Context): Location? {
        return ctx.player?.let { it.rayTraceBlocks(range)?.hitPosition?.toLocation(it.world) }
    }
}
