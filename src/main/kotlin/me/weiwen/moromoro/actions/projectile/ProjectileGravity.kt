@file:UseSerializers(ColorSerializer::class)

package me.weiwen.moromoro.actions.projectile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.serializers.ColorSerializer

@Serializable
@SerialName("projectile-gravity")
data class ProjectileGravity(val gravity: Boolean = false) : Action {
    override fun perform(ctx: Context): Boolean {
        val projectile = ctx.projectile ?: return false

        projectile.setGravity(gravity)

        return true
    }
}
