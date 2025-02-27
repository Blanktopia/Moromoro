package me.weiwen.moromoro.blocks

import me.weiwen.moromoro.Moromoro
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class EntityCustomBlock(override val block: Block, val entity: Entity, override val key: String) : CustomBlock {
    companion object {
        fun fromBlock(block: Block): EntityCustomBlock? {
            val location = block.location.add(0.5, 0.5, 0.5)

            val entities = location.world.getNearbyEntities(location, 0.5, 0.5, 0.5) {
                (it.type == EntityType.ITEM_FRAME || it.type == EntityType.ITEM_DISPLAY) &&
                        it.persistentDataContainer.has(
                            NamespacedKey(Moromoro.plugin.config.namespace, "type"),
                            PersistentDataType.STRING
                        )
            }

            if (entities.isEmpty()) {
                return null
            }

            val entity = entities.first() ?: return null
            val key = entity.persistentDataContainer.get(
                NamespacedKey(Moromoro.plugin.config.namespace, "type"),
                PersistentDataType.STRING
            ) ?: return null

            return EntityCustomBlock(block, entity, key)
        }

        fun fromEntity(entity: Entity): EntityCustomBlock? {
            val block = entity.location.block
            val key = entity.persistentDataContainer.get(
                NamespacedKey(Moromoro.plugin.config.namespace, "type"),
                PersistentDataType.STRING
            ) ?: return null

            return EntityCustomBlock(block, entity, key)
        }
    }

    override fun breakNaturally(tool: ItemStack?, dropItem: Boolean, location: Location?): Boolean {
        val ret = super.breakNaturally(tool, dropItem, entity.location)
        if (ret) {
            if (block.type == Material.BARRIER) {
                block.setType(Material.AIR, true)
            }
            entity.remove()
        }
        return ret
    }
}