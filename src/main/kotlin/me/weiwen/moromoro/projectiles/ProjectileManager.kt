package me.weiwen.moromoro.projectiles

import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.actions.Trigger
import me.weiwen.moromoro.managers.ItemManager
import org.bukkit.entity.Projectile
import java.util.*

class ProjectileManager(val plugin: Moromoro, val itemManager: ItemManager) {
    private val projectiles: WeakHashMap<Projectile,
            Pair<UUID?, List<Action>>> = WeakHashMap()

    private var tickTask: Int = -1

    fun enable() {
        tickTask = plugin.server.scheduler.scheduleSyncRepeatingTask(
            plugin,
            { ->
                projectiles.forEach { (projectile, playerActions) ->
                    if (projectile.isDead) {
                        return@forEach
                    }
                    val (uuid, actions) = playerActions
                    val ctx = Context(
                        event = null,
                        player = uuid?.let { plugin.server.getPlayer(it) },
                        item = null,
                        entity = null,
                        block = null,
                        blockFace = null,
                        projectile = projectile,
                    )
                    actions.forEach { it.perform(ctx) }
                }
            },
            plugin.config.tickInterval,
            plugin.config.tickInterval
        )
    }

    fun disable() {
        if (tickTask != -1) {
            plugin.server.scheduler.cancelTask(tickTask)
        }
    }

    fun register(projectile: Projectile, uuid: UUID?, key: String) {
        val actions = itemManager.triggers[key]?.get(Trigger.PROJECTILE_TICK) ?: return
        projectiles[projectile] = Pair(uuid, actions)
    }
}