package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit

@Serializable
@SerialName("remove-permission")
data class RemovePermission(val permissions: List<String>) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false

        val permissionsApi =
            Bukkit.getServer().servicesManager.getRegistration(Permission::class.java)?.provider ?: return false
        permissions.forEach {
            permissionsApi.playerRemove(player, it)
        }

        return true
    }
}
