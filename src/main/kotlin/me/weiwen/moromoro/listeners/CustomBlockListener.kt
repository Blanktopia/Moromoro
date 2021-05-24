package me.weiwen.moromoro.listeners

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.EnumWrappers
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.extensions.customItemKey
import me.weiwen.moromoro.extensions.isReallyInteractable
import me.weiwen.moromoro.extensions.playSoundAt
import me.weiwen.moromoro.managers.item
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.block.Block
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemFrame
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.hanging.HangingBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector
import java.util.logging.Level

class CustomBlockListener(val plugin: Moromoro) : Listener {
    init {
        val manager = ProtocolLibrary.getProtocolManager()

        manager.addPacketListener(object : PacketAdapter(plugin, PacketType.Play.Client.USE_ENTITY) {
            override fun onPacketReceiving(e: PacketEvent) {
                val packet = e.packet

                if (packet.entityUseActions.values[0] != EnumWrappers.EntityUseAction.ATTACK) {
                    return
                }

                val entityId = packet.integers.read(0)

                plugin.server.scheduler.scheduleSyncDelayedTask(plugin) {
                    val entity = e.player.world.getNearbyEntities(e.player.location, 10.0, 10.0, 10.0) {
                        it.entityId == entityId
                    }.firstOrNull() ?: return@scheduleSyncDelayedTask

                    // Break custom blocks
                    if (entity.type == EntityType.ITEM_FRAME) {
                        val key = entity.persistentDataContainer.get(
                            NamespacedKey(Moromoro.plugin.config.namespace, "type"),
                            PersistentDataType.STRING
                        ) ?: return@scheduleSyncDelayedTask
                        (entity as ItemFrame).breakCustomBlock()
                        e.isCancelled = true
                    }

                }
            }
        })
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val block = event.clickedBlock

        // Break custom blocks
        if (event.action == Action.LEFT_CLICK_BLOCK && block?.type == Material.BARRIER && block.breakCustomBlock()) {
            event.setUseItemInHand(Event.Result.DENY)
            event.setUseInteractedBlock(Event.Result.DENY)
            return
        }

        val item = when (event.hand) {
            EquipmentSlot.HAND -> event.player.inventory.itemInMainHand
            EquipmentSlot.OFF_HAND -> event.player.inventory.itemInOffHand
            else -> return
        }

        val key = item.customItemKey ?: return

        // Cancel if interacting with a block
        if (event.action == Action.RIGHT_CLICK_BLOCK && !event.player.isSneaking) {
            val blockType = event.clickedBlock?.type
            if (blockType?.isReallyInteractable == true) {
                event.setUseItemInHand(Event.Result.DENY)
                return
            }
        }

        val ctx = Context(
            event,
            event.player,
            item,
            null,
            event.clickedBlock,
            event.blockFace
        )

        val blockTemplate = plugin.itemManager.blockTemplates[key] ?: return
        if (event.action == Action.RIGHT_CLICK_BLOCK) {
            blockTemplate.place(ctx)
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onItemFrameBreak(event: HangingBreakEvent) {
        val key = event.entity.persistentDataContainer.get(
            NamespacedKey(Moromoro.plugin.config.namespace, "type"),
            PersistentDataType.STRING
        ) ?: return
        event.isCancelled = true
    }
}

fun Block.breakCustomBlock(): Boolean {
    val location = location.add(0.5, 0.5, 0.5)

    val itemFrames = world.getNearbyEntities(location, 0.5, 0.5, 0.5) {
        val key = it.persistentDataContainer.get(
            NamespacedKey(Moromoro.plugin.config.namespace, "type"),
            PersistentDataType.STRING
        ) ?: return@getNearbyEntities false
        it.type == EntityType.ITEM_FRAME
    }

    if (itemFrames.isEmpty()) {
        return false
    }

    val itemFrame = itemFrames.first() as? ItemFrame ?: return false

    itemFrame.breakCustomBlock()

    if (type == Material.BARRIER) {
        type = Material.AIR
    }

    return true
}

fun ItemFrame.breakCustomBlock() {
    persistentDataContainer.get(NamespacedKey(Moromoro.plugin.config.namespace, "type"), PersistentDataType.STRING) ?: return

    val item = item
    item.itemMeta = item.itemMeta.apply {
        val template = Moromoro.plugin.itemManager.templates[item.customItemKey] ?: return@apply
        val name = template.name?.value ?: return@apply
        setDisplayName(name)
    }

    remove()

    world.dropItemNaturally(this.location.clone().subtract(Vector(0.5, 0.5, 0.5)), item)

    playSoundAt(Sound.BLOCK_WOOD_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f)
}

