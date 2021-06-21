package me.weiwen.moromoro.actions.marker

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import org.bukkit.NamespacedKey

@Serializable
@SerialName("unmark-entity")
data class UnmarkEntity(val marker: String) : Action {
    override fun perform(ctx: Context): Boolean {
        val entity = ctx.entity ?: return false

        entity.persistentDataContainer.remove(NamespacedKey(Moromoro.plugin.config.namespace, "${MARKER_PREFIX}${marker}"))

        return true
    }
}
