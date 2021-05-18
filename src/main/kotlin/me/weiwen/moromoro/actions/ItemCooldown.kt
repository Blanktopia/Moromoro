package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("item-cooldown")
data class ItemCooldown(val world: String) : Action {
    override fun perform(ctx: Context): Boolean {
        return !ctx.player.hasCooldown(ctx.item.type)
    }
}

