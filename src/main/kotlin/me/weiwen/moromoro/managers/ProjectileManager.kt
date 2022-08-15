package me.weiwen.moromoro.managers

import me.weiwen.moromoro.Manager
import me.weiwen.moromoro.Moromoro.Companion.plugin
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.actions.Trigger
import me.weiwen.moromoro.items.ItemManager
import org.bukkit.entity.Projectile
import org.bukkit.scheduler.BukkitTask
import java.util.*

object ProjectileManager : Manager {
    private val projectiles: WeakHashMap<Projectile,
            Pair<UUID?, List<Action>>> = WeakHashMap()

    private var task: BukkitTask? = null

    override fun enable() {
        task = plugin.server.scheduler.runTaskTimer(
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

    override fun disable() {
        task?.cancel()
    }

    fun register(projectile: Projectile, uuid: UUID?, key: String) {
        val actions = ItemManager.triggers[key]?.get(Trigger.PROJECTILE_TICK) ?: return
        projectiles[projectile] = Pair(uuid, actions)
    }
}