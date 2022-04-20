package me.weiwen.moromoro.extensions

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.flags.Flags
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
    if (Bukkit.getServer().pluginManager.isPluginEnabled("WorldGuard")) {
        val player = WorldGuardPlugin.inst().wrapPlayer(this)

        val loc = BukkitAdapter.adapt(location)
        val container = WorldGuard.getInstance().platform.regionContainer
        val query = container.createQuery()
        if (!WorldGuard.getInstance().platform.sessionManager.hasBypass(player, BukkitAdapter.adapt(location.world)) &&
            !query.testState(loc, player, Flags.BUILD)
        ) {
            return false
        }
    }

    if (Bukkit.getServer().pluginManager.isPluginEnabled("GriefPrevention")) {
        val claim = GriefPrevention.instance.dataStore.getClaimAt(location, true, null) ?: return false
        if (claim.allowAccess(this) != null) {
            return false
        }
    }

    return true
}
