package me.weiwen.moromoro.actions

import de.Ste3et_C0st.ProtectionLib.main.ProtectionLib
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
        return ProtectionLib.getInstance().canBuild(loc, ctx.player)
    }
}

