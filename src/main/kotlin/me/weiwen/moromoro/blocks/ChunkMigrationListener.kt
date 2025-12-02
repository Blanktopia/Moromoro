package me.weiwen.moromoro.blocks

import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.items.ItemManager
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.BlockFace
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.ItemFrame
import org.bukkit.Chunk
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.persistence.PersistentDataType

object ChunkMigrationListener : Listener {

     @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
     fun onChunkLoad(event: ChunkLoadEvent) {
         migrateChunk(event.chunk)
     }

    fun migrateChunk(chunk: Chunk) {
        // Get all ItemFrames in the chunk that are custom blocks
        val itemFramesToMigrate = chunk.entities.filterIsInstance<ItemFrame>().filter { itemFrame ->
            itemFrame.persistentDataContainer.has(
                NamespacedKey(Moromoro.plugin.config.namespace, "type"),
                PersistentDataType.STRING
            )
        }

        if (itemFramesToMigrate.isEmpty()) {
            Moromoro.plugin.logger.info("No ItemFrame custom blocks found in chunk ${chunk.x}, ${chunk.z}")
            return
        }

        Moromoro.plugin.logger.info("Migrating ${itemFramesToMigrate.size} ItemFrame custom blocks to ItemDisplay in chunk ${chunk.x}, ${chunk.z}")

        // Schedule migration for next tick to ensure chunk is fully loaded
        Bukkit.getScheduler().runTask(Moromoro.plugin, Runnable {
            itemFramesToMigrate.forEach { itemFrame ->
                migrateItemFrameToDisplay(itemFrame)
            }
        })
    }

    private fun migrateItemFrameToDisplay(itemFrame: ItemFrame) {
        // Get the custom block key
        val key = itemFrame.persistentDataContainer.get(
            NamespacedKey(Moromoro.plugin.config.namespace, "type"),
            PersistentDataType.STRING
        ) ?: return

        // Get the item template
        val template = ItemManager.templates[key]
        if (template == null) {
            Moromoro.plugin.logger.warning("Failed to migrate ItemFrame at ${itemFrame.location}: template for key '$key' not found")
            return
        }

        // Check if this is actually a block template
        val blockTemplate = template.block as? ItemDisplayBlockTemplate
        if (blockTemplate == null) {
            Moromoro.plugin.logger.warning("Skipping migration of ItemFrame at ${itemFrame.location}: key '$key' is not an ItemDisplay block")
            return
        }

        // Get ItemFrame properties
        val location = itemFrame.location.block.location.add(0.5, 0.5, 0.5)
        val facing = itemFrame.facing
        val rotation = itemFrame.rotation
        val item = itemFrame.item

        if (item.type == Material.AIR) {
            Moromoro.plugin.logger.warning("Skipping migration of ItemFrame at ${itemFrame.location}: ItemFrame has no item")
            return
        }

        val hasCollision = location.block.type == Material.BARRIER

        // Calculate ItemDisplay rotation based on ItemFrame facing and rotation
        val rotationDegrees = rotation.rotationDegrees()

        when (facing) {
            BlockFace.UP -> {
                location.yaw = rotationDegrees + 180f
                location.pitch = 0f
            }
            BlockFace.DOWN -> {
                location.yaw = rotationDegrees + 180f
                location.pitch = 180f
            }
            BlockFace.NORTH -> {
                location.direction = BlockFace.NORTH.direction
                location.pitch = 90f
                location.yaw += rotationDegrees + 180f
            }
            BlockFace.SOUTH -> {
                location.direction = BlockFace.SOUTH.direction
                location.pitch = 90f
                location.yaw += rotationDegrees + 180f
            }
            BlockFace.EAST -> {
                location.direction = BlockFace.EAST.direction
                location.pitch = 90f
                location.yaw += rotationDegrees + 180f
            }
            BlockFace.WEST -> {
                location.direction = BlockFace.WEST.direction
                location.pitch = 90f
                location.yaw += rotationDegrees + 180f
            }
            else -> {
                location.yaw = rotationDegrees + 180f
                location.pitch = 0f
            }
        }

        // Apply template pitch/yaw offsets
        location.pitch += blockTemplate.pitch
        location.yaw += blockTemplate.yaw

        // Spawn ItemDisplay
        val itemDisplay = try {
            location.world.spawnEntity(location, EntityType.ITEM_DISPLAY) as ItemDisplay
        } catch (e: IllegalArgumentException) {
            Moromoro.plugin.logger.warning("Failed to spawn ItemDisplay at ${location}: ${e.message}")
            return
        }

        // Set ItemDisplay properties
        itemDisplay.apply {
            setItemStack(item)
            persistentDataContainer.set(
                NamespacedKey(Moromoro.plugin.config.namespace, "type"),
                PersistentDataType.STRING,
                key
            )
            persistentDataContainer.set(
                NamespacedKey(Moromoro.plugin.config.namespace, "migrated"),
                PersistentDataType.BOOLEAN,
                true
            )
        }

        // Remove the old ItemFrame
        itemFrame.remove()

        Moromoro.plugin.logger.fine("Migrated ItemFrame to ItemDisplay at ${location} (key: $key)")
    }

    private fun org.bukkit.Rotation.rotationDegrees(): Float {
        return when (this) {
            org.bukkit.Rotation.NONE -> 180f
            org.bukkit.Rotation.CLOCKWISE_45 -> 225f
            org.bukkit.Rotation.CLOCKWISE -> 270f
            org.bukkit.Rotation.CLOCKWISE_135 -> 315f
            org.bukkit.Rotation.FLIPPED -> 0f
            org.bukkit.Rotation.FLIPPED_45 -> 45f
            org.bukkit.Rotation.COUNTER_CLOCKWISE -> 90f
            org.bukkit.Rotation.COUNTER_CLOCKWISE_45 -> 135f
        }
    }
}
