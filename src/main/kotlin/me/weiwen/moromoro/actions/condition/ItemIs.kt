@file:UseSerializers(MaterialSerializer::class)

package me.weiwen.moromoro.actions.condition

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.serializers.MaterialSerializer
import org.bukkit.Material

@Serializable
@SerialName("item-is")
data class ItemIs(val material: Material) : Action {
    override fun perform(ctx: Context): Boolean {
        return ctx.item?.type == material
    }
}

