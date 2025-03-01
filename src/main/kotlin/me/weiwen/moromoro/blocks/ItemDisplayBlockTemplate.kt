@file:UseSerializers(
    ItemStackSerializer::class,
)

package me.weiwen.moromoro.blocks

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.BlockTrigger
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.blocks.autotile.AutoTile
import me.weiwen.moromoro.blocks.autotile.Sides
import me.weiwen.moromoro.extensions.customItemKey
import me.weiwen.moromoro.extensions.playSoundAt
import me.weiwen.moromoro.items.ItemManager
import me.weiwen.moromoro.items.item
import me.weiwen.moromoro.serializers.ItemStackSerializer
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.SoundCategory
import org.bukkit.block.BlockFace
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemDisplay
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import kotlin.math.roundToInt

@Serializable
@SerialName("item")
/* Placed using item display entities*/
class ItemDisplayBlockTemplate(
    private val collision: Boolean = false,

    @SerialName("sit-height")
    override val sitHeight: Double? = null,
    @SerialName("sit-rotate")
    override val sitRotate: Boolean? = null,

    override val drops: List<ItemStack>? = null,
    override val experience: Int = 0,
    @SerialName("can-fortune")
    override val canFortune: Boolean = false,

    override val hardness: Double = 1.0,
    override val tools: List<ItemStack>? = null,

    override val sounds: SoundGroup = SoundGroup(),

    override val triggers: Map<BlockTrigger, List<Action>> = mapOf(),

    @SerialName("auto-tile")
    val autoTile: AutoTile? = null,

    val pitch: Float = 0f,
    val yaw: Float = 0f,

    ) : BlockTemplate() {
    override fun place(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        val item = ctx.item ?: return false
        val key = ctx.item.customItemKey ?: return false

        val block = ctx.block ?: return false
        val blockFace = ctx.blockFace ?: BlockFace.UP

        val centerLocation = block.getRelative(blockFace).location.add(0.5, 0.5, 0.5)
        val nearbyItems = centerLocation.world.getNearbyEntities(centerLocation, 0.5, 0.5, 0.5) {
            it.type == EntityType.ITEM_FRAME || it.type == EntityType.ITEM_DISPLAY &&
                    it.persistentDataContainer.has(
                        NamespacedKey(Moromoro.plugin.config.namespace, "type"),
                        PersistentDataType.STRING
                    )
        }
        if (nearbyItems.isNotEmpty()) {
            return false
        }

        val location = block.getRelative(blockFace).location
        val world = location.world ?: return false

        // Try to place item display
        val itemDisplay = try {
            world.spawnEntity(location.add(0.5, 0.5, 0.5).let {
                if (blockFace == BlockFace.UP || blockFace == BlockFace.DOWN) {
                    val preciseYaw = player.location.yaw.plus(180)
                    it.yaw = if (player.isSneaking) {
                        preciseYaw
                    } else {
                        45f * (preciseYaw / 45).roundToInt()
                    }
                    if (blockFace == BlockFace.DOWN) {
                        it.pitch = 180f
                    }
                } else {
                    it.direction = blockFace.direction
                    it.pitch = 90f
                }
                it.pitch += pitch
                it
            }, EntityType.ITEM_DISPLAY) as ItemDisplay
        } catch (e: IllegalArgumentException) {
            return false
        }.apply {
            // Set persistent data
            persistentDataContainer.set(
                NamespacedKey(Moromoro.plugin.config.namespace, "type"),
                PersistentDataType.STRING,
                key
            )
        }

        // Move item into item display
        val sides = Sides(key, block.getRelative(blockFace), player.facing)
        val (renderKey, rotation) = autoTile?.autotile(sides) ?: Pair(key, 0f)
        val renderTemplate = ItemManager.templates[renderKey] ?: return false
        val renderYaw = (renderTemplate.block as? ItemDisplayBlockTemplate)?.yaw ?: 0f
        val renderItem = renderTemplate.item(key)
        val yaw = itemDisplay.yaw
        itemDisplay.setRotation(yaw + rotation + renderYaw, itemDisplay.pitch)
        itemDisplay.setItemStack(renderItem ?: item.clone())

        if (collision) {
            location.block.type = Material.BARRIER
        }

        sounds.place.let {
            location.block.playSoundAt(
                it?.sound ?: "block.wood.place",
                SoundCategory.BLOCKS,
                it?.volume ?: 1f,
                it?.pitch ?: 1f
            )
        }

        autoTile?.let { autoTile ->
            autoTile.neighbours(sides).forEach {
                if (it.key != key) return@forEach
                if (it !is EntityCustomBlock) return@forEach
                val entity = it.entity as? ItemDisplay ?: return@forEach
//                if (entity.facing != itemDisplay.facing) return@forEach
                val sides = Sides(key, it.block, player.facing)
                val (renderKey, rotation) = autoTile.autotile(sides) ?: return@forEach
                val renderTemplate = ItemManager.templates[renderKey] ?: return@forEach
                val renderYaw = (renderTemplate.block as? ItemDisplayBlockTemplate)?.yaw ?: 0f
                val renderItem = renderTemplate.item(key)
                entity.setRotation(yaw + rotation + renderYaw, entity.pitch)
                entity.setItemStack(renderItem)
            }
        }

        return true
    }
}
