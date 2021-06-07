package me.weiwen.moromoro.listeners

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.EnumWrappers
import me.gsit.api.GSitAPI
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.actions.Trigger
import me.weiwen.moromoro.extensions.*
import me.weiwen.moromoro.managers.customBlockState
import me.weiwen.moromoro.managers.item
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemFrame
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.hanging.HangingBreakEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector
import java.util.logging.Level

class CustomBlockListener(val plugin: Moromoro) : Listener {
    val gSitApi: GSitAPI by lazy { GSitAPI() }

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

                    if (!e.player.canBuildAt(entity.location)) {
                        return@scheduleSyncDelayedTask
                    }

                    // Break custom blocks
                    if (entity.type == EntityType.ITEM_FRAME) {
                        if (!entity.persistentDataContainer.has(
                                NamespacedKey(Moromoro.plugin.config.namespace, "type"),
                                PersistentDataType.STRING
                            )
                        ) {
                            return@scheduleSyncDelayedTask
                        }
                        (entity as ItemFrame).breakCustomBlock()
                        e.isCancelled = true
                    }

                }
            }
        })
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    fun onBlockBreak(event: BlockBreakEvent) {
        val block = event.block

        if (block.customBlockState == null) {
            return
        }

        event.isCancelled = true

        plugin.blockManager.breakNaturally(block, event.player.gameMode != GameMode.CREATIVE)
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val block = event.clickedBlock

        // Break custom blocks
        if (event.action == Action.LEFT_CLICK_BLOCK && block?.type == Material.BARRIER) {
            if (!event.player.canBuildAt(block.location)) {
                return
            }
            if (block.breakCustomBlock()) {
                event.setUseItemInHand(Event.Result.DENY)
                event.setUseInteractedBlock(Event.Result.DENY)
                return
            }
        }

        // Prevent double interaction
        if (event.useInteractedBlock() == Event.Result.DENY) {
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

        val blockTemplate = plugin.blockManager.blockTemplates[key] ?: return

        event.setUseItemInHand(Event.Result.DENY)

        if (event.action == Action.RIGHT_CLICK_BLOCK && block != null) {
            if (!event.player.canBuildAt(block.location)) {
                return
            }

            val blockFace = event.blockFace

            val (placedAgainst, targetFace) = when (block.type) {
                Material.GRASS, Material.TALL_GRASS, Material.FERN, Material.LARGE_FERN, Material.SNOW -> Pair(
                    block.getRelative(BlockFace.DOWN), BlockFace.UP
                )
                else -> Pair(block, blockFace)
            }
            val placedBlock = placedAgainst.getRelative(targetFace)

            if (event.player.location.block.location == placedBlock.location
                || event.player.location.add(0.0, 1.0, 0.0).block.location == placedBlock.location
            ) {
                return
            }

            val ctx = Context(
                event,
                event.player,
                item,
                null,
                placedAgainst,
                targetFace
            )

            if (blockTemplate.place(ctx)) {
                if (event.player.gameMode != GameMode.CREATIVE) {
                    item.amount -= 1
                }
                block.playSoundAt(Sound.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f)
                when (event.hand) {
                    EquipmentSlot.HAND -> event.player.swingMainHand()
                    EquipmentSlot.OFF_HAND -> event.player.swingOffHand()
                    else -> {
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        // Cancel if interacting with a block
        if (event.player.isSneaking) {
            return
        }

        val entity = event.rightClicked

        // Sit
        if (entity.type == EntityType.ITEM_FRAME && entity is ItemFrame) {
            val key = entity.persistentDataContainer.get(
                NamespacedKey(Moromoro.plugin.config.namespace, "type"),
                PersistentDataType.STRING
            ) ?: return

            val sitHeight = plugin.blockManager.blockTemplates[key]?.sitHeight ?: return

            val offset = Vector(sitHeight, sitHeight, sitHeight).multiply(entity.facing.direction)
            val seatLocation = entity.location.toBlockLocation().apply {
                rotation = entity.rotation
            }

            gSitApi.setPlayerSeat(
                event.player,
                seatLocation,
                offset.x,
                offset.y,
                offset.z,
                seatLocation.yaw,
                entity.origin,
                false,
                true
            )
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onItemFrameBreak(event: HangingBreakEvent) {
        if (!event.entity.persistentDataContainer.has(
                NamespacedKey(Moromoro.plugin.config.namespace, "type"),
                PersistentDataType.STRING
            )
        ) {
            return
        }

        event.isCancelled = true
    }
}

fun Block.breakCustomBlock(): Boolean {
    val location = location.add(0.5, 0.5, 0.5)

    val itemFrames = world.getNearbyEntities(location, 0.5, 0.5, 0.5) {
        it.type == EntityType.ITEM_FRAME &&
                it.persistentDataContainer.has(
                    NamespacedKey(Moromoro.plugin.config.namespace, "type"),
                    PersistentDataType.STRING
                )
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
    persistentDataContainer.get(NamespacedKey(Moromoro.plugin.config.namespace, "type"), PersistentDataType.STRING)
        ?: return

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

