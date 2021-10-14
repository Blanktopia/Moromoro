package me.weiwen.moromoro.actions.effect

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context

@Serializable
@SerialName("explode")
data class Explode(
    val power: Float,
    @SerialName("set-fire") val setFire: Boolean = false,
    @SerialName("break-blocks") val breakBlocks: Boolean = false,
) : Action {
    override fun perform(ctx: Context): Boolean {
        val entity = ctx.entity
        val location =
            ctx.projectile?.location
                ?: entity?.location?.clone()?.add(0.0, entity.height, 0.0)
                ?: ctx.block?.location
                ?: return false

        location.world.createExplosion(location, power, setFire, breakBlocks, ctx.player)

        return true
    }
}

