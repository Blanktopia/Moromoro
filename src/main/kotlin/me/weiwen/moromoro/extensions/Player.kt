package me.weiwen.moromoro.extensions

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.flags.Flags
import me.ryanhamshire.GriefPrevention.Claim
import me.ryanhamshire.GriefPrevention.GriefPrevention
import me.weiwen.moromoro.hooks.ShulkerPacksHook
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.ShulkerBox
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BlockStateMeta
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

private val claimCache = ConcurrentHashMap<UUID, Claim?>()

fun Player.hasAccessTrust(location: Location): Boolean {
    if (Bukkit.getServer().pluginManager.isPluginEnabled("GriefPrevention")) {
        val cachedClaim = claimCache[uniqueId]
        val claim = GriefPrevention.instance.dataStore.getClaimAt(location, true, cachedClaim) ?: return false
        claimCache[uniqueId] = claim
        return claim.allowAccess(this) == null
    }

    return false
}

fun Player.canBuildAt(location: Location): Boolean {
    if (Bukkit.getServer().pluginManager.isPluginEnabled("WorldGuard")) {
        val player = WorldGuardPlugin.inst().wrapPlayer(this)

        val loc = BukkitAdapter.adapt(location)
        val container = WorldGuard.getInstance().platform.regionContainer
        if (!WorldGuard.getInstance().platform.sessionManager.hasBypass(player, BukkitAdapter.adapt(location.world)) &&
            !container.createQuery().testBuild(loc, player, Flags.BLOCK_PLACE)
        ) {
            return false
        }
    }

    if (Bukkit.getServer().pluginManager.isPluginEnabled("GriefPrevention")) {
        val cachedClaim = claimCache[uniqueId]
        val claim = GriefPrevention.instance.dataStore.getClaimAt(location, true, cachedClaim) ?: return true
        claimCache[uniqueId] = claim
        if (claim.allowBuild(this, Material.STONE) != null) {
            return false
        }
    }

    return true
}

fun Player.hasAtLeastInInventoryOrShulkerBoxes(itemStack: ItemStack): Boolean {
    var remainder = itemStack.amount

    if (inventory.containsAtLeast(itemStack, itemStack.amount)) {
        return true
    }

    val contents = inventory.storageContents
    for (i in contents.indices) {
        val item = contents[i] ?: continue
        if (item.type !in shulkerBoxes) {
            continue
        }
        val blockStateMeta = item.itemMeta as? BlockStateMeta ?: continue
        val shulkerBox = blockStateMeta.blockState as? ShulkerBox ?: continue

        if (ShulkerPacksHook.isShulkerBoxOpen(item)) {
            continue
        }

        if (!shulkerBox.inventory.filterNotNull().any { it.type == itemStack.type }) {
            continue
        }

        shulkerBox.inventory.filterNotNull().forEach {
            remainder -= it.amount
            if (remainder <= 0) {
                return true
            }
        }
    }

    return false
}

fun Player.removeItemFromInventoryOrShulkerBoxes(
    itemStack: ItemStack,
    removeFromShulkerBoxesFirst: Boolean = true
): ItemStack? {
    var remainder = itemStack.amount

    if (!removeFromShulkerBoxesFirst) {
        val remainingItemStacks = inventory.removeItem(itemStack)
        if (remainingItemStacks.isEmpty()) {
            return null
        } else {
            remainder = remainingItemStacks.get(0)?.amount ?: 0
        }
    }

    val contents = inventory.storageContents
    for (i in contents.indices) {
        val item = contents[i] ?: continue
        if (item.type !in shulkerBoxes) {
            continue
        }
        val blockStateMeta = item.itemMeta as? BlockStateMeta ?: continue
        val shulkerBox = blockStateMeta.blockState as? ShulkerBox ?: continue

        if (ShulkerPacksHook.isShulkerBoxOpen(item)) {
            continue
        }

        if (!shulkerBox.inventory.filterNotNull().any { it.type == itemStack.type }) {
            continue
        }

        val toRemove = itemStack.clone().apply {
            amount = remainder
        }
        val shulkerBoxCouldntRemove = shulkerBox.inventory.removeItem(itemStack)[0]
        blockStateMeta.blockState = shulkerBox
        item.itemMeta = blockStateMeta
        contents[i] = item

        remainder = shulkerBoxCouldntRemove?.let {
            it.amount
        } ?: 0

        if (remainder == 0) {
            return null
        }
    }

    if (removeFromShulkerBoxesFirst) {
        val remainingItemStacks = inventory.removeItem(itemStack.clone().apply { amount = remainder })
        if (remainingItemStacks.isEmpty()) {
            return null
        } else {
            remainder = remainingItemStacks.get(0)?.amount ?: 0
        }
    }

    val remainderItemStack = itemStack.clone().apply {
        amount = remainder
    }
    return remainderItemStack
}
