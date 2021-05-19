package me.weiwen.moromoro.extensions

import me.ryanhamshire.GriefPrevention.GriefPrevention
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player

fun Player.isInOwnClaim(location: Location): Boolean {
    val claim = GriefPrevention.instance.dataStore.getClaimAt(location, true, null) ?: return false
    return claim.ownerID == uniqueId
}

fun Player.hasAccessTrust(location: Location): Boolean {
    val claim = GriefPrevention.instance.dataStore.getClaimAt(location, true, null) ?: return false
    return claim.allowAccess(this) == null
}

fun Player.hasContainerTrust(location: Location): Boolean {
    val claim = GriefPrevention.instance.dataStore.getClaimAt(location, true, null) ?: return false
    return claim.allowContainers(this) == null
}

fun Player.hasBuildTrust(location: Location, material: Material): Boolean {
    val claim = GriefPrevention.instance.dataStore.getClaimAt(location, true, null) ?: return false
    return claim.allowBuild(this, material) == null
}

fun Player.canBuildAt(location: Location): Boolean {
    return GriefPrevention.instance.allowBuild(this, location) == null
}
