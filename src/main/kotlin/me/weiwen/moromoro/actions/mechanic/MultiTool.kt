package me.weiwen.moromoro.actions.mechanic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.extensions.canMineBlock
import org.bukkit.Material

@Serializable
@SerialName("multi-tool")
data class MultiTool(val tools: List<Material>) : Action {
    override fun perform(ctx: Context): Boolean {
        val block = ctx.block ?: return false
        val item = ctx.item ?: return false

        if (item.type.canMineBlock(block)) return false

        for (tool in tools) {
            if (tool.canMineBlock(block)) {
                item.type = tool
                return true
            }
        }
        return false
    }
}
