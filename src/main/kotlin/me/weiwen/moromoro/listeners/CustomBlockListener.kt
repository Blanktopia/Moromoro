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
import org.bukkit.Material
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

                plugin.logger.log(Level.INFO, "entityID looking for: $entityId")

                plugin.server.scheduler.scheduleSyncDelayedTask(plugin) {
                    val entity = e.player.world.getNearbyEntities(e.player.location, 10.0, 10.0, 10.0) {
                        plugin.logger.log(Level.INFO, "entityID found: ${it.entityId}")
                        it.entityId == entityId
                    }.firstOrNull() ?: return@scheduleSyncDelayedTask

                    // Break custom blocks
                    if (entity.type == EntityType.ITEM_FRAME) {
                        // TODO: check persistent data
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
        // TODO: check persistent data
        event.isCancelled = true
    }
}

fun Block.breakCustomBlock(): Boolean {
    val location = location.add(0.5, 0.5, 0.5)

    val itemFrames = world.getNearbyEntities(location, 0.5, 0.5, 0.5) {
        it.type == EntityType.ITEM_FRAME
        // TODO: check persistent data
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
    val item = item
    item.itemMeta = item.itemMeta.apply {
        val template = Moromoro.plugin.itemManager.templates[item.customItemKey] ?: return@apply
        val name = template.name?.value ?: return@apply
        setDisplayName(name)
    }
    remove()
    world.dropItemNaturally(this.location, item)

    playSoundAt(Sound.BLOCK_WOOD_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f)
}

