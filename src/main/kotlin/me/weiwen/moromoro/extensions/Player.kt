package me.weiwen.moromoro.extensions

import de.Ste3et_C0st.ProtectionLib.main.ProtectionLib
import me.ryanhamshire.GriefPrevention.GriefPrevention
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

fun Player.hasAccessTrust(location: Location): Boolean {
    if (Bukkit.getServer().pluginManager.isPluginEnabled("GriefPrevention")) {
        val claim = GriefPrevention.instance.dataStore.getClaimAt(location, true, null) ?: return false
        return claim.allowAccess(this) == null
    }

    return false
}

fun Player.canBuildAt(location: Location): Boolean {
    return ProtectionLib.getInstance().canBuild(location, this)
}
