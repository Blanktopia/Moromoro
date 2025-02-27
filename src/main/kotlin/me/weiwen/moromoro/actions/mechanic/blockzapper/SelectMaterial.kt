package me.weiwen.moromoro.actions.mechanic.blockzapper

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.extensions.playSoundAt
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material
import org.bukkit.SoundCategory
import org.bukkit.inventory.ItemStack
import java.util.*

@Serializable
@SerialName("select-material")
object SelectMaterial : Action {
    val materials: MutableMap<UUID, Material> = mutableMapOf()

    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false
        val block = ctx.block ?: return false

        materials[player.uniqueId] = block.type

        val message = Component.text("Selected: ${ItemStack(block.type).i18NDisplayName}").color(TextColor.color(0xffaa00))
        player.sendActionBar(message)

        player.playSoundAt(block.blockSoundGroup.placeSound, SoundCategory.BLOCKS, 1.0f, 0.5f)

        return true
    }
}

