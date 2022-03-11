package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Location

@Serializable
enum class LocationSelector {
    @SerialName("player")
    PLAYER,

    @SerialName("entity")
    ENTITY,

    @SerialName("block")
    BLOCK,

    @SerialName("projectile")
    PROJECTILE,

    @SerialName("raycast")
    RAYCAST,
}

fun LocationSelector.center(ctx: Context): Location? {
    return when (this) {
        LocationSelector.PLAYER -> ctx.player?.let { it.location.clone().add(0.0, it.height / 2, 0.0) }
        LocationSelector.ENTITY -> ctx.entity?.let { it.location.clone().add(0.0, it.height / 2, 0.0) }
        LocationSelector.BLOCK -> ctx.block?.let { it.location.clone().add(0.5, 0.5, 0.5) }
        LocationSelector.PROJECTILE -> ctx.projectile?.let { it.location.clone().add(0.0, it.height / 2, 0.0) }
        LocationSelector.RAYCAST -> ctx.player?.let { it.rayTraceBlocks(5.0)?.hitPosition?.toLocation(it.world) }
    }
}

fun LocationSelector.location(ctx: Context): Location? {
    return when (this) {
        LocationSelector.PLAYER -> ctx.player?.let { it.location }
        LocationSelector.ENTITY -> ctx.entity?.let { it.location }
        LocationSelector.BLOCK -> ctx.block?.let { it.location.clone().add(0.5, 0.0, 0.5) }
        LocationSelector.PROJECTILE -> ctx.projectile?.let { it.location }
        LocationSelector.RAYCAST -> ctx.player?.let { it.rayTraceBlocks(5.0)?.hitPosition?.toLocation(it.world) }
    }
}
