package me.weiwen.moromoro.items

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.actions.EQUIPPED_TRIGGERS
import me.weiwen.moromoro.actions.Trigger
import me.weiwen.moromoro.extensions.customItemKey
import me.weiwen.moromoro.extensions.playSoundTo
import me.weiwen.moromoro.managers.CustomEquipmentSlot
import me.weiwen.moromoro.managers.ItemManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

class TrinketManager(val plugin: Moromoro, private val itemManager: ItemManager) {
    private val trinketTriggers: MutableMap<UUID,
            MutableMap<Trigger,
                    MutableMap<Int, Pair<ItemStack, List<Action>>>>> = mutableMapOf()

    private var tickTask: Int = -1
    private var tickSlowTask: Int = -1

    fun enable() {
        trinketTriggers.clear()
        for (player in plugin.server.onlinePlayers) {
            runEquipTriggers(player)
        }

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

    fun runEquipTriggers(player: Player) {
        val trinkets = player.trinkets

        val triggersMap = itemManager.triggers

        trinkets.forEachIndexed { slot, item ->
            val key = item?.customItemKey ?: return@forEachIndexed

            // Skip if in wrong slot
            val slots = itemManager.templates[key]?.slots ?: return@forEachIndexed
            if (!slots.contains(CustomEquipmentSlot.TRINKET)) {
                return
            }

            val triggers = triggersMap[key] ?: return@forEachIndexed

            triggers.forEach { (trigger, actions) ->
                if (trigger in EQUIPPED_TRIGGERS) {
                    trinketTriggers
                        .getOrPut(player.uniqueId) { mutableMapOf() }
                        .getOrPut(trigger) { mutableMapOf() }[slot] = Pair(item, actions)
                }
            }

            val ctx = Context(
                null,
                player,
                item,
                null,
                null,
                null
            )

            triggers[Trigger.EQUIP_ARMOR]?.forEach { it.perform(ctx) }
        }
    }

    fun runEquipTriggers(event: Event?, player: Player, trigger: Trigger) {
        trinketTriggers[player.uniqueId]?.get(trigger)?.values?.forEach { (item, triggers) ->
            val ctx = Context(event, player, item, null, null, null)

            triggers.forEach { it.perform(ctx) }

            if (event is Cancellable && ctx.isCancelled) {
                event.isCancelled = true
            }
        }
    }

    fun equipTrinket(player: Player, item: ItemStack, slot: Int? = null): Boolean {
        val trinkets = player.trinkets
        val slot = slot ?: trinkets.indexOfFirst { it == null }
        if (slot == -1) {
            return false
        }

        val keys = trinkets.mapNotNull { it?.customItemKey }
        if (keys.contains(item.customItemKey)) {
            return false
        }

        if (item?.type != Material.AIR) {
            player.persistentDataContainer.set(
                NamespacedKey(Moromoro.plugin.config.namespace, "trinkets_$slot"),
                PersistentDataType.BYTE_ARRAY,
                item.serializeAsBytes()
            )
        } else {
            player.persistentDataContainer.remove(
                NamespacedKey(Moromoro.plugin.config.namespace, "trinkets_$slot"),
            )
        }

        val key = item.customItemKey
        val triggers = itemManager.triggers[key] ?: return true

        triggers.forEach { (triggerType, actions) ->
            if (triggerType in EQUIPPED_TRIGGERS) {
                trinketTriggers
                    .getOrPut(player.uniqueId) { mutableMapOf() }
                    .getOrPut(triggerType) { mutableMapOf() }[slot] = Pair(item, actions)
            }
        }

        val ctx = Context(null, player, item, null, null, null, null)

        val template = itemManager.templates[key] ?: return true
        template.triggers[Trigger.EQUIP_ARMOR]?.forEach {
            it.perform(ctx)
        }

        return true
    }

    fun unequipTrinket(player: Player, slot: Int): ItemStack? {
        val oldItem = player.trinket(slot)

        player.persistentDataContainer.remove(
            NamespacedKey(Moromoro.plugin.config.namespace, "trinkets_$slot"),
        )

        val key = oldItem?.customItemKey ?: return oldItem
        val triggers = itemManager.triggers[key] ?: return oldItem

        triggers.forEach { (triggerType, _) ->
            if (triggerType in EQUIPPED_TRIGGERS) {
                val triggersByType = trinketTriggers[player.uniqueId] ?: return@forEach
                val triggers = triggersByType[triggerType] ?: return@forEach
                triggers.remove(slot)
                if (triggers.isEmpty()) {
                    triggersByType.remove(triggerType)
                }
            }
        }

        val ctx = Context(null, player, oldItem, null, null, null, null)

        val template = itemManager.templates[key] ?: return oldItem
        template.triggers[Trigger.UNEQUIP_ARMOR]?.forEach {
            it.perform(ctx)
        }

        return oldItem
    }

    fun openTrinketInventory(player: Player) {
        val gui = ChestGui(2, "Trinkets")

        val trinketPane = OutlinePane(0, 0, 9, 2)

        val guiItems = player.trinkets.mapIndexed { i, item ->
            val guiItem = if (item != null) {
                GuiItem(item)
            } else {
                GuiItem(ItemStack(Material.AIR))
            }
            trinketPane.insertItem(guiItem, i)
            guiItem
        }.toMutableList()

        gui.setOnTopDrag { event ->
            event.isCancelled = true
        }

        gui.setOnBottomClick { event ->
            if (event.action == InventoryAction.PICKUP_HALF || event.action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                val newItem = event.currentItem ?: ItemStack(Material.AIR)
                if (newItem.type == Material.AIR) {
                    event.isCancelled = true
                    return@setOnBottomClick
                }

                val key = newItem.customItemKey
                val template = itemManager.templates[key]
                if (template == null || !template.slots.contains(CustomEquipmentSlot.TRINKET)) {
                    if (event.action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                        player.sendActionBar(
                            Component.text("You can only place trinkets here.").color(TextColor.color(0xff5555))
                        )
                        player.playSoundTo(Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, SoundCategory.PLAYERS, 1.0f, 1.0f)
                        event.isCancelled = true
                    }
                    return@setOnBottomClick
                }

                val emptySlot = guiItems.indexOfFirst { it.item.type == Material.AIR }
                if (emptySlot == -1) {
                    event.isCancelled = true
                    return@setOnBottomClick
                }

                if (!equipTrinket(player, newItem, emptySlot)) {
                    event.isCancelled = true
                    return@setOnBottomClick
                }
                event.clickedInventory?.setItem(event.slot, null)

                val newGuiItem = GuiItem(newItem)
                val oldGuiItem = guiItems[emptySlot]
                guiItems[emptySlot] = newGuiItem
                trinketPane.removeItem(oldGuiItem)
                trinketPane.insertItem(newGuiItem, emptySlot)

                gui.update()
            }
        }

        trinketPane.setOnClick { event ->
            event.isCancelled = true
            if (event.action == InventoryAction.PLACE_ALL || event.action == InventoryAction.PLACE_ONE || event.action == InventoryAction.PICKUP_ALL) {
                val newItem = event.cursor ?: ItemStack(Material.AIR)

                if (newItem.type != Material.AIR) {
                    val key = newItem.customItemKey
                    val template = itemManager.templates[key]
                    if (template == null || !template.slots.contains(CustomEquipmentSlot.TRINKET)) {
                        player.sendActionBar(
                            Component.text("You can only place trinkets here.").color(TextColor.color(0xff5555))
                        )
                        player.playSoundTo(Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, SoundCategory.PLAYERS, 1.0f, 1.0f)
                        return@setOnClick
                    }
                }

                val newGuiItem = GuiItem(newItem)
                val oldGuiItem = guiItems[event.slot]

                val oldItem = unequipTrinket(player, event.slot)
                if (!equipTrinket(player, newItem, event.slot)) {
                    return@setOnClick
                }
                event.cursor = oldItem

                guiItems[event.slot] = newGuiItem
                trinketPane.removeItem(oldGuiItem)
                trinketPane.insertItem(newGuiItem, event.slot)

                gui.update()
            } else if (event.action == InventoryAction.PICKUP_HALF || event.action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                val oldGuiItem = guiItems[event.slot]
                val oldItem = player.trinket(event.slot) ?: return@setOnClick

                val didntAdd = player.inventory.addItem(oldItem)
                if (didntAdd.size == 0) {
                    unequipTrinket(player, event.slot)

                    val newGuiItem = GuiItem(ItemStack(Material.AIR))
                    guiItems[event.slot] = newGuiItem
                    trinketPane.removeItem(oldGuiItem)
                    trinketPane.insertItem(newGuiItem, event.slot)
                }

                gui.update()
            }
        }

        gui.addPane(trinketPane)

        gui.show(player)
    }

    fun cleanUp(player: Player) {
        trinketTriggers.remove(player.uniqueId)
    }
}

fun Player.trinket(i: Int): ItemStack? {
    return persistentDataContainer.get(
        NamespacedKey(Moromoro.plugin.config.namespace, "trinkets_$i"),
        PersistentDataType.BYTE_ARRAY
    )?.let { ItemStack.deserializeBytes(it) }
}

val Player.trinkets: List<ItemStack?>
    get() = (0..17).map { i ->
        persistentDataContainer.get(
            NamespacedKey(Moromoro.plugin.config.namespace, "trinkets_$i"),
            PersistentDataType.BYTE_ARRAY
        )?.let { ItemStack.deserializeBytes(it) }
    }

