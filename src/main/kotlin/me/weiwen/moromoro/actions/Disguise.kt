package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.libraryaddict.disguise.disguisetypes.*
import me.libraryaddict.disguise.disguisetypes.Disguise
import me.weiwen.moromoro.Moromoro
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack

@Serializable
sealed class DisguiseData {
    abstract val burning: Boolean
}

@Serializable
@SerialName("mob")
data class MobDisguiseData(val entity: EntityType, val baby: Boolean = false, override val burning: Boolean = false) :
    DisguiseData()

@Serializable
@SerialName("player")
data class PlayerDisguiseData(val player: String, override val burning: Boolean = false) : DisguiseData()

@Serializable
@SerialName("block")
data class BlockDisguiseData(val material: Material, val data: Int = 0, override val burning: Boolean = false) :
    DisguiseData()

@Serializable
@SerialName("item")
data class ItemDisguiseData(val material: Material, val amount: Int = 1, override val burning: Boolean = false) :
    DisguiseData()

val DisguiseData.disguise: Disguise
    get() = when (this) {
        is MobDisguiseData -> MobDisguise(DisguiseType.getType(entity), !baby)
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
    val invisible: Boolean = false
) : Action {
    override fun perform(ctx: Context): Boolean {
        if (!Moromoro.plugin.server.pluginManager.isPluginEnabled("LibsDisguises")) {
            return false
        }

        val player = ctx.player ?: return false
        val burning = disguise.burning
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
