package me.weiwen.moromoro.blocks

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.WrappedEnumEntityUseAction
import dev.geco.gsit.api.GSitAPI
import io.papermc.paper.event.block.BlockBreakBlockEvent
import me.weiwen.moromoro.Moromoro.Companion.plugin
import me.weiwen.moromoro.actions.BlockTrigger
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.extensions.canBuildAt
import me.weiwen.moromoro.extensions.customItemKey
import me.weiwen.moromoro.extensions.isReallyInteractable
import me.weiwen.moromoro.managers.BlockManager
import me.weiwen.moromoro.managers.customBlockState
import me.weiwen.moromoro.packets.WrapperPlayClientBlockDig
import me.weiwen.moromoro.packets.blockFace
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.ItemFrame
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPhysicsEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.hanging.HangingBreakEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.util.Vector

object BlockListener : Listener {

    init {
        val manager = ProtocolLibrary.getProtocolManager()

        manager.removePacketListeners(plugin)

        manager.addPacketListener(object : PacketAdapter(plugin, PacketType.Play.Client.USE_ENTITY) {
            override fun onPacketReceiving(e: PacketEvent) {
                val packet = e.packet

                val useAction: WrappedEnumEntityUseAction = packet.enumEntityUseActions.read(0)
                val action: EnumWrappers.EntityUseAction = useAction.action

                if (action != EnumWrappers.EntityUseAction.ATTACK) {
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
                    val customBlock = EntityCustomBlock.fromEntity(entity) ?: return@scheduleSyncDelayedTask
                    customBlock.breakNaturally(null, true)

                    e.isCancelled = true
                }
            }
        })

        manager.addPacketListener(object : PacketAdapter(plugin, PacketType.Play.Client.BLOCK_DIG) {
            override fun onPacketReceiving(e: PacketEvent) {
                val packet = WrapperPlayClientBlockDig(e.packet)

                plugin.server.scheduler.scheduleSyncDelayedTask(plugin) {
                    val location = packet.location?.toLocation(e.player.world) ?: return@scheduleSyncDelayedTask
                    val customBlock = CustomBlock.fromBlock(location.block) ?: return@scheduleSyncDelayedTask
                    val direction = packet.direction ?: return@scheduleSyncDelayedTask

                    when (packet.status) {
                        EnumWrappers.PlayerDigType.START_DESTROY_BLOCK -> BlockManager.startDigging(
                            e.player,
                            customBlock,
                            direction.blockFace
                        )

                        EnumWrappers.PlayerDigType.ABORT_DESTROY_BLOCK -> BlockManager.cancelDigging(e.player)
                        else -> {}
                    }
                }
            }
        })
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onMushroomPhysics(event: BlockPhysicsEvent) {
        if (event.changedType == Material.BROWN_MUSHROOM_BLOCK || event.changedType == Material.RED_MUSHROOM_BLOCK || event.changedType == Material.MUSHROOM_STEM) {
            event.isCancelled = true
            event.block.state.update(true, false)
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onMushroomBlockPlace(event: BlockPlaceEvent) {
        val block = event.block
        if (block.type == Material.BROWN_MUSHROOM_BLOCK || block.type == Material.RED_MUSHROOM_BLOCK || block.type == Material.MUSHROOM_STEM) {
            event.block.customBlockState = 0b111111
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBlockBreakBlock(event: BlockBreakBlockEvent) {
        val block = event.block

        // Mushroom Blocks
        val customBlock = MushroomCustomBlock.fromBlock(block)
        if (customBlock != null) {
            event.drops.clear()
            if (customBlock.breakNaturally(null, true)) {
                val triggers = customBlock.template?.block?.triggers?.get(BlockTrigger.BLOCK_BREAK)
                if (triggers != null) {
                    val ctx = Context(
                        event,
                        null,
                        null,
                        null,
                        customBlock.block,
                        null,
                        null,
                    )
                    triggers.forEach { it.perform(ctx) }
                }
            }
            return
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        val block = event.block

        // Mushroom Blocks
        val customBlock = MushroomCustomBlock.fromBlock(block)
        if (customBlock != null) {
            event.isCancelled = true
            if (customBlock.breakNaturally(event.player.inventory.itemInMainHand, event.player.gameMode != GameMode.CREATIVE)) {
                val triggers = customBlock.template?.block?.triggers?.get(BlockTrigger.BLOCK_BREAK)
                if (triggers != null) {
                    val ctx = Context(
                        event,
                        event.player,
                        null,
                        null,
                        customBlock.block,
                        null,
                        null,
                    )
                    triggers.forEach { it.perform(ctx) }
                }
            }
            return
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val block = event.clickedBlock ?: return

        // Item Frame Barrier Blocks
        if (event.action == Action.LEFT_CLICK_BLOCK) {
            val customBlock =
                EntityCustomBlock.fromBlock(block.getRelative(event.blockFace)) ?: EntityCustomBlock.fromBlock(block)
            if (customBlock != null && event.player.canBuildAt(block.location)) {
                if (customBlock.breakNaturally(event.player.inventory.itemInMainHand, true)) {
                    val triggers = customBlock.template?.block?.triggers?.get(BlockTrigger.BLOCK_BREAK)
                    if (triggers != null) {
                        val ctx = Context(
                            event,
                            event.player,
                            null,
                            null,
                            customBlock.block,
                            null,
                            null,
                        )
                        triggers.forEach { it.perform(ctx) }
                    }
                    event.setUseItemInHand(Event.Result.DENY)
                    event.setUseInteractedBlock(Event.Result.DENY)
                    return
                }
            }
            return
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

        val customBlock = EntityCustomBlock.fromBlock(block.getRelative(event.blockFace)) ?: EntityCustomBlock.fromBlock(block)
        if (!event.player.isSneaking && event.hand == EquipmentSlot.HAND && customBlock != null) {
            // Triggers
            val triggers = customBlock.template?.block?.triggers?.get(BlockTrigger.BLOCK_USE)
             if (triggers != null) {
                val ctx = Context(
                    event,
                    event.player,
                    null,
                    null,
                    customBlock.block,
                    null,
                    null,
                    customBlock,
                )
                triggers.forEach { it.perform(ctx) }
                return
            }

            // Sit
            else if (event.hand == EquipmentSlot.HAND && event.action == Action.RIGHT_CLICK_BLOCK && item.type == Material.AIR) {
                val blockTemplate = BlockManager.blockTemplates[customBlock.key] ?: return
                val sitHeight = blockTemplate.sitHeight ?: return
                val sitRotate = blockTemplate.sitRotate
                val yaw = if (blockTemplate is ItemDisplayBlockTemplate && customBlock.entity is ItemDisplay) {
                    -blockTemplate.yaw
                } else {
                    0f
                }

                val offset = Vector(0.0, sitHeight, 0.0).rotateAroundY(customBlock.entity.location.pitch.toDouble())
                val seatLocation = customBlock.entity.location.add(0.0, -1.0, 0.0)

                GSitAPI.createSeat(
                    seatLocation.block,
                    event.player,
                    sitRotate ?: false,
                    offset.x,
                    offset.y,
                    offset.z,
                    seatLocation.yaw + yaw,
                    true
                )

                event.setUseItemInHand(Event.Result.DENY)

                return
            }
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

        val blockTemplate = BlockManager.blockTemplates[key] ?: return

        event.setUseItemInHand(Event.Result.DENY)

        if (event.action == Action.RIGHT_CLICK_BLOCK) {
            if (!event.player.canBuildAt(block.location)) {
                return
            }

            val blockFace = event.blockFace

            val (placedAgainst, targetFace) = if (block.isReplaceable) {
                Pair(block.getRelative(BlockFace.DOWN), BlockFace.UP)
            } else {
                Pair(block, blockFace)
            }
            val placedBlock = placedAgainst.getRelative(targetFace)

            if (!placedBlock.isReplaceable) {
                return
            }

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
                blockTemplate.triggers.get(BlockTrigger.BLOCK_PLACE)?.forEach { it.perform(ctx) }
                if (event.player.gameMode != GameMode.CREATIVE) {
                    item.amount -= 1
                }
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

        val item = when (event.hand) {
            EquipmentSlot.HAND -> event.player.inventory.itemInMainHand
            EquipmentSlot.OFF_HAND -> event.player.inventory.itemInOffHand
            else -> return
        }
        if (item.type != Material.AIR) {
            return
        }

        val itemFrame = event.rightClicked as? ItemFrame ?: return
        val customBlock = EntityCustomBlock.fromEntity(itemFrame) ?: return

        // Triggers
        val triggers = customBlock.template?.block?.triggers?.get(BlockTrigger.BLOCK_USE)
        if (triggers != null) {
            val ctx = Context(
                event,
                event.player,
                null,
                null,
                customBlock.block,
                null,
                null,
                customBlock,
            )
            triggers.forEach { it.perform(ctx) }
        }

        // Sit
        val blockTemplate = BlockManager.blockTemplates[customBlock.key] ?: return
        val sitHeight = blockTemplate.sitHeight ?: return
        val sitRotate = blockTemplate.sitRotate
        val yaw = if (blockTemplate is ItemDisplayBlockTemplate && customBlock.entity is ItemDisplay) {
            -blockTemplate.yaw
        } else {
            0f
        }

        val offset = Vector(0.0, sitHeight, 0.0).rotateAroundY(customBlock.entity.location.pitch.toDouble())
        val seatLocation = customBlock.entity.location.add(0.0, -1.0, 0.0)

        GSitAPI.createSeat(
            seatLocation.block,
            event.player,
            sitRotate ?: false,
            offset.x,
            offset.y,
            offset.z,
            yaw,
            true
        )


        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onItemFrameBreak(event: HangingBreakEvent) {
        val itemFrame = event.entity as? ItemFrame ?: return
        val customBlock = EntityCustomBlock.fromEntity(itemFrame) ?: return

        customBlock.breakNaturally(null, true)

        event.isCancelled = true
    }
}