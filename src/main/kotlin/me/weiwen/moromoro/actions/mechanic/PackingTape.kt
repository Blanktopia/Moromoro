package me.weiwen.moromoro.actions.mechanic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.extensions.canBuildAt
import me.weiwen.moromoro.extensions.playSoundAt
import org.bukkit.*
import org.bukkit.block.Container
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BlockStateMeta

val packableBlocks = setOf(
    Material.CHEST,
    Material.BARREL,
    Material.DISPENSER,
    Material.DROPPER,
    Material.HOPPER,
)

@Serializable
@SerialName("packing-tape")
object PackingTape : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        val block = ctx.block ?: return false

        if (!player.canBuildAt(block.location)) {
            return false
        }

        if (block.type !in packableBlocks) {
            return false
        }

        val container = block.state as? Container ?: return false
        Moromoro.plugin.logger.info("container is container")

        val item = ItemStack(block.type)
        val blockStateMeta = item.itemMeta as? BlockStateMeta ?: return false
        blockStateMeta.blockState = container
        item.itemMeta = blockStateMeta

        block.playSoundAt(Sound.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 1.0f, 1.0f)
        block.type = Material.AIR
        block.world.dropItem(block.location.add(0.5, 0.5, 0.5), item)

        return true
    }
}

