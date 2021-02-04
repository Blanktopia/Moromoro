package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.extensions.canMineBlock
import org.bukkit.Material

@Serializable
@SerialName("multi-tool")
data class MultiTool(val materials: List<Material>) : Action {
    override fun perform(ctx: Context): Boolean {
        val block = ctx.block ?: return false
        if (ctx.item.type.canMineBlock(block)) return false

        for (material in materials) {
            if (material.canMineBlock(block)) {
                ctx.item.type = material
                return true
            }
        }
        return false
    }
}
