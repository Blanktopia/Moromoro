package me.weiwen.moromoro.items

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.actions.EQUIPPED_TRIGGERS
import me.weiwen.moromoro.actions.Trigger
import me.weiwen.moromoro.extensions.customItemKey
import me.weiwen.moromoro.extensions.equipmentSlot
import me.weiwen.moromoro.managers.customEquipmentSlot
import me.weiwen.moromoro.types.customEquipmentSlot
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import java.util.*

class EquippedItemsManager(private val plugin: Moromoro, private val itemManager: ItemManager) {
    private val equippedItems: MutableMap<UUID,
            MutableMap<Trigger,
                    MutableMap<PlayerArmorChangeEvent.SlotType, Pair<ItemStack, List<Action>>>>> = mutableMapOf()

    private var tickTask: Int = -1
    private var tickSlowTask: Int = -1

    fun enable() {
        runEquipTriggers()

        tickTask = plugin.server.scheduler.scheduleSyncRepeatingTask(
            plugin,
            { ->
                plugin.server.onlinePlayers.forEach {
                    runEquipTriggers(null, it, Trigger.TICK)
                }
            },
            plugin.config.tickInterval,
            plugin.config.tickInterval
        )
        tickSlowTask = plugin.server.scheduler.scheduleSyncRepeatingTask(
            plugin,
            { ->
                plugin.server.onlinePlayers.forEach {
                    runEquipTriggers(null, it, Trigger.TICK_SLOW)
                }
            },
            plugin.config.tickSlowInterval,
            plugin.config.tickSlowInterval
        )
    }

    fun disable() {
        if (tickTask != -1) {
            plugin.server.scheduler.cancelTask(tickTask)
        }
        if (tickSlowTask != -1) {
            plugin.server.scheduler.cancelTask(tickSlowTask)
        }
    }

    private fun runEquipTriggers() {
        equippedItems.clear()
        for (player in plugin.server.onlinePlayers) {
            val slots: MutableMap<PlayerArmorChangeEvent.SlotType, ItemStack> = mutableMapOf()
            player.inventory.helmet?.let { slots[PlayerArmorChangeEvent.SlotType.HEAD] = it }
            player.inventory.chestplate?.let { slots[PlayerArmorChangeEvent.SlotType.CHEST] = it }
            player.inventory.leggings?.let { slots[PlayerArmorChangeEvent.SlotType.LEGS] = it }
            player.inventory.boots?.let { slots[PlayerArmorChangeEvent.SlotType.FEET] = it }

            val triggersMap = itemManager.triggers

            slots.forEach { (slot, item) ->
                val key = item.customItemKey ?: return@forEach

                // Skip if in wrong slot
                val slots = itemManager.templates[key]?.slots ?: return@forEach
                if (slots.isNotEmpty() && slot.equipmentSlot.customEquipmentSlot in slots) {
                    return@forEach
                }

                val triggers = triggersMap[key] ?: return@forEach

                triggers.forEach { (trigger, actions) ->
                    if (trigger in EQUIPPED_TRIGGERS) {
                        equippedItems
                            .getOrPut(player.uniqueId) { mutableMapOf() }
                            .getOrPut(trigger) { mutableMapOf() }[slot] = Pair(item, actions)
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

    fun onPlayerArmorChange(event: PlayerArmorChangeEvent) {
        if (event.newItem == event.oldItem) return

        event.oldItem?.let { item ->
            val key = item.customItemKey ?: return@let

            // Skip if in wrong slot
            val slots = itemManager.templates[key]?.slots ?: return
            if (slots.isNotEmpty() && event.slotType.equipmentSlot.customEquipmentSlot !in slots) {
                return
            }

            val triggers = itemManager.triggers[key] ?: return@let

            triggers.forEach { (triggerType, _) ->
                if (triggerType in EQUIPPED_TRIGGERS) {
                    val triggersByType = equippedItems[event.player.uniqueId] ?: return@forEach
                    val triggers = triggersByType[triggerType] ?: return@forEach
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

            // Skip if in wrong slot
            val slots = itemManager.templates[key]?.slots ?: return
            if (slots.isNotEmpty() && event.slotType.equipmentSlot.customEquipmentSlot !in slots) {
                return
            }

            val triggers = itemManager.triggers[key] ?: return@let

            triggers.forEach { (triggerType, actions) ->
                if (triggerType in EQUIPPED_TRIGGERS) {
                    equippedItems
                        .getOrPut(event.player.uniqueId) { mutableMapOf() }
                        .getOrPut(triggerType) { mutableMapOf() }[event.slotType] = Pair(item, actions)
                }
            }

            val trigger = when (event.slotType) {
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

    fun runEquipTriggers(event: Event?, player: Player, trigger: Trigger) {
        runTriggers(event, player, player.inventory.itemInMainHand, EquipmentSlot.HAND, trigger)
        runTriggers(event, player, player.inventory.itemInOffHand, EquipmentSlot.OFF_HAND, trigger)

        equippedItems[player.uniqueId]?.get(trigger)?.values?.forEach { (item, triggers) ->
            val ctx = Context(event, player, item, null, null, null)

            triggers.forEach { it.perform(ctx) }

            if (event is Cancellable && ctx.isCancelled) {
                event.isCancelled = true
            }
        }
    }

    fun cleanUp(player: Player) {
        equippedItems.remove(player.uniqueId)
    }

    private fun runTriggers(event: Event?, player: Player, item: ItemStack, slot: EquipmentSlot, trigger: Trigger) {
        val key = item.customItemKey ?: return

        // Skip if in wrong slot
        val slots = itemManager.templates[key]?.slots ?: return
        if (slots.isNotEmpty() && slot.customEquipmentSlot !in slots) {
            return
        }

        val triggers = itemManager.triggers[key] ?: return

        val ctx = Context(event, player, item, null, null, null)

        triggers[trigger]?.forEach { it.perform(ctx) }

        if (event is Cancellable && ctx.isCancelled) {
            event.isCancelled = true
        }
    }

}