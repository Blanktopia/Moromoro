package me.weiwen.moromoro.actions.mechanic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.extensions.isRightTool
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

@Serializable
@SerialName("multi-tool")
data class MultiTool(val tools: List<Material>) : Action {
    override fun perform(ctx: Context): Boolean {
        val block = ctx.block ?: return false
        val item = ctx.item ?: return false

        if (block.isRightTool(item)) return false

        for (tool in tools) {
            if (block.isRightTool(ItemStack(tool))) {
                item.type = tool
                return true
            }
        }
        return false
    }
}
