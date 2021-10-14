@file:UseSerializers(MaterialSerializer::class)

package me.weiwen.moromoro.actions.condition

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.serializers.MaterialSerializer
import org.bukkit.entity.EntityType

@Serializable
@SerialName("entity-is")
data class EntityIs(val entity: EntityType) : Action {
    override fun perform(ctx: Context): Boolean {
        val e = ctx.entity ?: return false
        return e.type == entity
    }
}

