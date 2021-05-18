package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.extensions.canMineBlock
import org.bukkit.Material

@Serializable
@SerialName("multi-tool")
data class MultiTool(val tools: List<Material>) : Action {
    override fun perform(ctx: Context): Boolean {
        val block = ctx.block ?: return false
        if (ctx.item.type.canMineBlock(block)) return false

        for (tool in tools) {
            if (tool.canMineBlock(block)) {
                ctx.item.type = tool
                return true
            }
        }
        return false
    }
}