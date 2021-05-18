package me.weiwen.moromoro.managers

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import com.destroystokyo.paper.event.player.PlayerJumpEvent
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.actions.EQUIPPED_TRIGGERS
import me.weiwen.moromoro.actions.Trigger
import me.weiwen.moromoro.extensions.customItemKey
import me.weiwen.moromoro.listeners.PlayerListener
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityToggleGlideEvent
import org.bukkit.event.entity.EntityToggleSwimEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack
import java.util.*

class EquippedItemsManager(private val plugin: Moromoro) : Listener {
    private val equippedItems: MutableMap<UUID,
            MutableMap<Trigger,
                    MutableMap<PlayerArmorChangeEvent.SlotType, Pair<ItemStack, List<Action>>>>> = mutableMapOf()

    fun enable() {
        plugin.server.pluginManager.registerEvents(EquippedItemsManager(plugin), plugin)
        runEquipTriggers()
    }

    fun disable() {}

    private fun runEquipTriggers() {
        equippedItems.clear()
        for (player in plugin.server.onlinePlayers) {
            val slots: MutableMap<PlayerArmorChangeEvent.SlotType, ItemStack> = mutableMapOf()
            player.inventory.helmet?.let { slots[PlayerArmorChangeEvent.SlotType.HEAD] = it }
            player.inventory.chestplate?.let { slots[PlayerArmorChangeEvent.SlotType.CHEST] = it }
            player.inventory.leggings?.let { slots[PlayerArmorChangeEvent.SlotType.LEGS] = it }
            player.inventory.boots?.let { slots[PlayerArmorChangeEvent.SlotType.FEET] = it }

            val triggersMap = plugin.itemManager.triggers

            for ((slot, item) in slots.entries) {
                val key = item.customItemKey ?: return
                val triggers = triggersMap[key] ?: return

                triggers.forEach { (trigger, actions) ->
                    if (trigger in EQUIPPED_TRIGGERS) {
                        equippedItems
                            .getOrPut(player.uniqueId, { mutableMapOf() })
                            .getOrPut(trigger, { mutableMapOf() })[slot] = Pair(item, actions)
                    }
                }

                val trigger = when (slot) {
                    PlayerArmorChangeEvent.SlotType.HEAD -> Trigger.EQUIP_HEAD
                    PlayerArmorChangeEvent.SlotType.CHEST -> Trigger.EQUIP_CHEST
                    PlayerArmorChangeEvent.SlotType.LEGS -> Trigger.EQUIP_LEGS
                    PlayerArmorChangeEvent.SlotType.FEET -> Trigger.EQUIP_FEET
                }

                val ctx = Context(
                    null,
                    player,
                    item,
                    null,
                    null,
                    null
                )

                triggers[trigger]?.forEach { it.perform(ctx) }
                triggers[Trigger.EQUIP_ARMOR]?.forEach { it.perform(ctx) }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerArmorChange(event: PlayerArmorChangeEvent) {
        if (event.newItem == event.oldItem) return

        event.oldItem?.let { item ->
            val key = item.customItemKey ?: return@let
            val triggers = plugin.itemManager.triggers[key] ?: return@let

            triggers.forEach { triggerType, _ ->
                if (triggerType in EQUIPPED_TRIGGERS) {
                    val triggersByType = equippedItems.get(event.player.uniqueId) ?: return@forEach
                    val triggers = triggersByType.get(triggerType) ?: return@forEach
                    triggers.remove(event.slotType)
                    if (triggers.isEmpty()) {
                        triggersByType.remove(triggerType)
                    }
                }
            }

            val ctx = Context(
                event,
                event.player,
                item,
                null,
                null,
                null
            )

            triggers[Trigger.UNEQUIP_ARMOR]?.forEach { it.perform(ctx) }
        }

        event.newItem?.let { item ->
            val key = item.customItemKey ?: return@let
            val triggers = plugin.itemManager.triggers[key] ?: return@let

            triggers.forEach { triggerType, actions ->
                if (triggerType in EQUIPPED_TRIGGERS) {
                    equippedItems
                        .getOrPut(event.player.uniqueId, { mutableMapOf() })
                        .getOrPut(triggerType, { mutableMapOf() })[event.slotType] = Pair(item, actions)
                }
            }
            val trigger= when (event.slotType) {
                PlayerArmorChangeEvent.SlotType.HEAD -> Trigger.EQUIP_HEAD
                PlayerArmorChangeEvent.SlotType.CHEST -> Trigger.EQUIP_CHEST
                PlayerArmorChangeEvent.SlotType.LEGS -> Trigger.EQUIP_LEGS
                PlayerArmorChangeEvent.SlotType.FEET -> Trigger.EQUIP_FEET
            }

            val ctx = Context(
                event,
                event.player,
                item,
                null,
                null,
                null
            )

            triggers[trigger]?.forEach { it.perform(ctx) }
            triggers[Trigger.EQUIP_ARMOR]?.forEach { it.perform(ctx) }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerMove(event: PlayerMoveEvent) {
        equippedItems[event.player.uniqueId]?.get(Trigger.MOVE)?.values?.forEach { (item, triggers) ->
            val ctx = Context(
                event,
                event.player,
                item,
                null,
                null,
                null
            )

            triggers.forEach { it.perform(ctx) }

            if (ctx.isCancelled) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerJump(event: PlayerJumpEvent) {
        equippedItems[event.player.uniqueId]?.get(Trigger.JUMP)?.values?.forEach { (item, triggers) ->
            val ctx = Context(
                event,
                event.player,
                item,
                null,
                null,
                null
            )

            triggers.forEach { it.perform(ctx) }

            if (ctx.isCancelled) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerToggleSneak(event: PlayerToggleSneakEvent) {
        val trigger = if (event.isSneaking) Trigger.SNEAK else Trigger.UNSNEAK
        equippedItems[event.player.uniqueId]?.get(trigger)?.values?.forEach { (item, triggers) ->
            val ctx = Context(
                event,
                event.player,
                item,
                null,
                null,
                null
            )

            triggers.forEach { it.perform(ctx) }

            if (ctx.isCancelled) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerToggleSprint(event: PlayerToggleSprintEvent) {
        val trigger = if (event.isSprinting) Trigger.SPRINT else Trigger.UNSPRINT
        equippedItems[event.player.uniqueId]?.get(trigger)?.values?.forEach { (item, triggers) ->
            val ctx = Context(
                event,
                event.player,
                item,
                null,
                null,
                null
            )

            triggers.forEach { it.perform(ctx) }

            if (ctx.isCancelled) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerToggleFlight(event: PlayerToggleFlightEvent) {
        val trigger = if (event.isFlying) Trigger.FLY else Trigger.UNFLY
        equippedItems[event.player.uniqueId]?.get(trigger)?.values?.forEach { (item, triggers) ->
            val ctx = Context(
                event,
                event.player,
                item,
                null,
                null,
                null
            )

            triggers.forEach { it.perform(ctx) }

            if (ctx.isCancelled) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerToggleGlide(event: EntityToggleGlideEvent) {
        val player = event.entity as? Player ?: return
        val trigger = if (event.isGliding) Trigger.GLIDE else Trigger.UNGLIDE
        equippedItems[player.uniqueId]?.get(trigger)?.values?.forEach { (item, triggers) ->
            val ctx = Context(
                event,
                player,
                item,
                null,
                null,
                null
            )

            triggers.forEach { it.perform(ctx) }

            if (ctx.isCancelled) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerToggleSwim(event: EntityToggleSwimEvent) {
        val player = event.entity as? Player ?: return
        val trigger = if (event.isSwimming) Trigger.SWIM else Trigger.UNSWIM
        equippedItems[player.uniqueId]?.get(trigger)?.values?.forEach { (item, triggers) ->
            val ctx = Context(
                event,
                player,
                item,
                null,
                null,
                null
            )

            triggers.forEach { it.perform(ctx) }

            if (ctx.isCancelled) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        // Cleanup
        equippedItems.remove(event.player.uniqueId)
    }
}