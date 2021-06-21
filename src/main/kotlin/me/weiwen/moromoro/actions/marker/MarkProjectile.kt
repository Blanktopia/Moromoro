package me.weiwen.moromoro.actions.marker

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataType

@Serializable
@SerialName("mark-projectile")
data class MarkProjectile(val marker: String) : Action {
    override fun perform(ctx: Context): Boolean {
        val projectile = ctx.projectile ?: return false

        projectile.persistentDataContainer.set(NamespacedKey(Moromoro.plugin.config.namespace, "${MARKER_PREFIX}${marker}"), PersistentDataType.SHORT, 1)

        return true
    }
}
