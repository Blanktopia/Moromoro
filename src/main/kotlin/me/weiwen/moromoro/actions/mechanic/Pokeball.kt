@file:UseSerializers(EntityTypeSerializer::class)

package me.weiwen.moromoro.actions.mechanic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.extensions.canBuildAt
import me.weiwen.moromoro.extensions.playSoundAt
import me.weiwen.moromoro.serializers.EntityTypeSerializer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.*
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.persistence.PersistentDataType

@Serializable
@SerialName("pokeball")
class Pokeball(private val blacklist: List<EntityType> = listOf(EntityType.ENDER_DRAGON, EntityType.WITHER, EntityType.WARDEN)) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        val item = ctx.item ?: return false
        val itemMeta = item.itemMeta ?: return false

        val serializedEntity = item.itemMeta?.persistentDataContainer?.get(NamespacedKey(Moromoro.plugin.config.namespace, "entity"), PersistentDataType.BYTE_ARRAY)
        if (serializedEntity != null) {
            val world = player.world ?: return false
            val deserializedEntity = Bukkit.getUnsafe().deserializeEntity(serializedEntity, world, true)
            val vector = player.rayTraceBlocks(5.0)?.hitPosition ?: return false
            val location = Location(world, vector.x, vector.y, vector.z)

            if (!player.canBuildAt(location)) {
                return false
            }

            try {
                deserializedEntity.spawnAt(location)
            } catch (e: Exception) {
                return false
            }

            val message = Component.text("Caught: NONE")
                .decoration(TextDecoration.ITALIC, false)
                .color(TextColor.color(0xffffff))

            val lore = itemMeta.lore()
            if (lore != null) {
                lore[1] = message
                itemMeta.lore(lore)
            }

            itemMeta.persistentDataContainer.remove(NamespacedKey(Moromoro.plugin.config.namespace, "entity"))

            item.itemMeta = itemMeta

            deserializedEntity.playSoundAt(Sound.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 1.0f, 1.0f)
        } else {
            val entity = ctx.entity as? LivingEntity ?: return false

            if (!player.canBuildAt(entity.location)) {
                val message = Component.text("You don't have permission here.")
                    .decoration(TextDecoration.ITALIC, false)
                    .color(TextColor.color(0xffffff))
                player.sendActionBar(message)
                return false
            }

            if (entity.type == EntityType.PLAYER) {
                return false
            }

            if (entity.type in blacklist) {
                val message = Component.text("This creature seems to resist capturing.")
                    .decoration(TextDecoration.ITALIC, false)
                    .color(TextColor.color(0xffffff))
                player.sendActionBar(message)
                return false
            }

            entity.rider?.leaveVehicle()
            entity.leaveVehicle()

            val serializedEntity = try {
                Bukkit.getUnsafe().serializeEntity(entity)
            } catch (e: Exception) {
                return false
            }

            val message = Component.text("Caught: ${entity.name}")
                .decoration(TextDecoration.ITALIC, false)
                .color(TextColor.color(0xffffff))
            player.sendActionBar(message)

            val lore = itemMeta.lore()
            if (lore != null) {
                lore[1] = message
                itemMeta.lore(lore)
            }

            itemMeta.persistentDataContainer.set(NamespacedKey(Moromoro.plugin.config.namespace, "entity"), PersistentDataType.BYTE_ARRAY, serializedEntity)

            item.itemMeta = itemMeta

            entity.playSoundAt(Sound.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 1.0f, 2.0f)
            entity.remove()
        }

        return true
    }
}

