package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.MoromoroConfig
import me.weiwen.moromoro.extensions.color
import me.weiwen.moromoro.extensions.playSoundAt
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.DyeColor
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.persistence.PersistentDataType

@Serializable
@SerialName("paint-brush-pick")
object PaintBrushPick : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        val item = ctx.item ?: return false
        val block = ctx.block ?: return false

        val colour = block.type.color

        val paint = when (colour) {
            null -> "NONE"
            DyeColor.WHITE -> "WHITE"
            DyeColor.ORANGE -> "ORANGE"
            DyeColor.MAGENTA -> "MAGENTA"
            DyeColor.LIGHT_BLUE -> "LIGHT BLUE"
            DyeColor.YELLOW -> "YELLOW"
            DyeColor.LIME -> "LIME"
            DyeColor.PINK -> "PINK"
            DyeColor.GRAY -> "GRAY"
            DyeColor.LIGHT_GRAY -> "LIGHT GRAY"
            DyeColor.CYAN -> "CYAN"
            DyeColor.PURPLE -> "PURPLE"
            DyeColor.BLUE -> "BLUE"
            DyeColor.BROWN -> "BROWN"
            DyeColor.GREEN -> "GREEN"
            DyeColor.RED -> "RED"
            DyeColor.BLACK -> "BLACK"
        }

        val meta = item.itemMeta ?: return false

        val lore = meta.lore ?: return false
        lore[2] = "§7Paint: ${paint}"

        meta.lore = lore

        val data = meta.persistentDataContainer

        if (colour == null) {
            data.remove(NamespacedKey(Moromoro.plugin.config.namespace, "paint"))
        } else {
            data.set(NamespacedKey(Moromoro.plugin.config.namespace, "paint"), PersistentDataType.STRING, paint)
        }

        item.itemMeta = meta

        val message = TextComponent("Paint: ${paint}")
        message.setColor(ChatColor.GOLD)
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message)

        player.playSoundAt(Sound.BLOCK_SLIME_BLOCK_STEP, SoundCategory.BLOCKS, 1.0f, 0.5f)

        return true
    }
}

