package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.extensions.playSoundAt
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.ItemFrame

@Serializable
@SerialName("toggle-item-frame-visibility")
object ToggleItemFrameVisibility : Action {
    override fun perform(ctx: Context): Boolean {
        val entity = ctx.entity ?: return false
        val itemFrame = entity as? ItemFrame ?: return false

        itemFrame.isVisible = !itemFrame.isVisible

        entity.playSoundAt(Sound.BLOCK_GRINDSTONE_USE, SoundCategory.PLAYERS, 1.0f, 2.0f)

        return true
    }
}

