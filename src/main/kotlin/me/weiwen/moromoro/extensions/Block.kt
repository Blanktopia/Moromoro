package me.weiwen.moromoro.extensions

import com.comphenix.protocol.wrappers.BlockPosition
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.blocks.CustomBlock
import me.weiwen.moromoro.packets.WrapperPlayServerBlockBreakAnimation
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack

fun Block.sendBlockDamage(progress: Float, entityId: Int) {
    if (Bukkit.getServer().pluginManager.isPluginEnabled("ProtocolLib")) {
        val packet = WrapperPlayServerBlockBreakAnimation()
        packet.entityID = entityId
        packet.location = BlockPosition(location.blockX, location.blockY, location.blockZ)
        packet.destroyStage = if (progress == 0f) {
            -1
        } else {
            (progress * 10).toInt()
        }

        val distance = Moromoro.plugin.config.renderDistance * Moromoro.plugin.config.renderDistance
        Bukkit.getServer().onlinePlayers.forEach { player ->
            if (player.world == world && player.location.distanceSquared(location) < distance) {
                packet.sendPacket(player)
            }
        }
    }
}

fun Block.isRightTool(item: ItemStack): Boolean {
    val customBlock = CustomBlock.fromBlock(this)
    return if (customBlock != null) {
        val tools = customBlock.template?.block?.tools ?: return false
        tools.any { itemStack -> itemStack.type == item.type }
    } else {
        if (item.type.isPickaxe) {
            Tag.MINEABLE_PICKAXE.isTagged(type)
        } else if (item.type.isAxe) {
            Tag.MINEABLE_AXE.isTagged(type)
        } else if (item.type.isShovel) {
            Tag.MINEABLE_SHOVEL.isTagged(type)
        } else if (item.type.isHoe) {
            Tag.MINEABLE_HOE.isTagged(type)
        } else if (item.type == Material.SHEARS) {
            Tag.LEAVES.isTagged(type)
                    || Tag.WOOL_CARPETS.isTagged(type)
                    || Tag.WOOL.isTagged(type)
                    || Tag.CROPS.isTagged(type)
                    || Tag.FLOWERS.isTagged(type)
                    || type in setOf(Material.SHORT_GRASS, Material.TALL_GRASS, Material.SEAGRASS, Material.TALL_SEAGRASS, Material.FERN, Material.LARGE_FERN, Material.VINE)
        } else {
            false
        }
    }
}