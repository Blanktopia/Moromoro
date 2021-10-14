@file:UseSerializers(MaterialSerializer::class)

package me.weiwen.moromoro.actions.block

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.serializers.MaterialSerializer
import org.bukkit.Material

@Serializable
@SerialName("set-block")
data class SetBlock(val material: Material) : Action {
    override fun perform(ctx: Context): Boolean {
        val block = ctx.block ?: return false
        block.type = material
        return true
    }
}
