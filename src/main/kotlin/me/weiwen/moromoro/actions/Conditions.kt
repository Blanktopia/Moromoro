package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.extensions.canBuildAt

@Serializable
@SerialName("is-in-world")
data class IsInWorld(val world: String) : Action {
    override fun perform(ctx: Context): Boolean {
        return ctx.player.world.name == world
    }
}

@Serializable
@SerialName("can-build")
object CanBuild : Action {
    override fun perform(ctx: Context): Boolean {
        val loc = ctx.block?.location ?: return false
        return ctx.player.canBuildAt(loc)
    }
}

