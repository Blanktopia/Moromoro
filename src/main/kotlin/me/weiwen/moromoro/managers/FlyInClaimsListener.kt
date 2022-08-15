package me.weiwen.moromoro.managers

import me.weiwen.moromoro.extensions.hasAccessTrust
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTeleportEvent
import java.util.*

object FlyInClaimsListener : Listener {
    private val canFlyPlayers: MutableSet<UUID> = mutableSetOf()

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        checkCanFly(event.player, event.to, event.from)
    }

    @EventHandler
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        checkCanFly(event.player, event.to, event.from)
    }

    private fun checkCanFly(player: Player, to: Location?, from: Location) {
        if (to == null) return
        if (to.blockX == from.blockX && to.blockZ == from.blockZ) return
        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) return
        if (player.canFlyInClaims) {
            player.allowFlight = player.hasAccessTrust(to)
        }
    }

    var Player.canFlyInClaims: Boolean
        get() = canFlyPlayers.contains(uniqueId)
        set(canFly) {
            if (canFly) {
                canFlyPlayers.add(uniqueId)
                allowFlight =
                    gameMode == GameMode.CREATIVE || gameMode == GameMode.SPECTATOR || hasAccessTrust(location)
            } else {
                canFlyPlayers.remove(uniqueId)
                if (gameMode != GameMode.CREATIVE && gameMode != GameMode.SPECTATOR) {
                    allowFlight = false
                }
            }
        }
}

