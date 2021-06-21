package me.weiwen.moromoro.actions.marker

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataType

val MARKER_PREFIX = "marker/"

@Serializable
@SerialName("entity-has-marker")
data class EntityHasMarker(val marker: String) : Action {
    override fun perform(ctx: Context): Boolean {
        val entity = ctx.entity ?: return false

        return entity.persistentDataContainer.has(NamespacedKey(Moromoro.plugin.config.namespace, "${MARKER_PREFIX}${marker}"), PersistentDataType.SHORT)
    }
}
