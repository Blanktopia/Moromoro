package me.weiwen.moromoro.actions

import me.weiwen.moromoro.extensions.canMineBlock
import org.bukkit.Material

class CycleToolAction(private val materials: List<Material>) : Action {
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
