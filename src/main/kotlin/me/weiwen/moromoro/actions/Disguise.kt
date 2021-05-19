package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.libraryaddict.disguise.disguisetypes.*
import me.libraryaddict.disguise.disguisetypes.Disguise
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

@Serializable
sealed class DisguiseData

@Serializable
@SerialName("mob")
data class MobDisguiseData(val entity: DisguiseType, val baby: Boolean = false) : DisguiseData()

@Serializable
@SerialName("player")
data class PlayerDisguiseData(val player: String) : DisguiseData()

@Serializable
@SerialName("block")
data class BlockDisguiseData(val material: Material, val data: Int = 0) : DisguiseData()

@Serializable
@SerialName("item")
data class ItemDisguiseData(val material: Material, val amount: Int = 1) : DisguiseData()

val DisguiseData.disguise: Disguise
    get() = when (this) {
        is MobDisguiseData -> MobDisguise(entity, baby)
        is PlayerDisguiseData -> PlayerDisguise(player)
        is BlockDisguiseData -> MiscDisguise(DisguiseType.FALLING_BLOCK, material, data)
        is ItemDisguiseData -> {
            val itemStack = ItemStack(material, amount)
            MiscDisguise(DisguiseType.DROPPED_ITEM, itemStack)
        }
    }

@Serializable
@SerialName("disguise")
data class Disguise(
    val disguise: DisguiseData,
    val burning: Boolean = false,
    val invisible: Boolean = false
) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player
        val disguise = disguise.disguise
        disguise.entity = player
        disguise.watcher.apply {
            isBurning = burning
            isInvisible = invisible
        }
        disguise.startDisguise()
        return true
    }
}
