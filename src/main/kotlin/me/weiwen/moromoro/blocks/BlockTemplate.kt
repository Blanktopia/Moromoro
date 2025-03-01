@file:UseSerializers(
    ItemStackSerializer::class,
)

package me.weiwen.moromoro.blocks

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.BlockTrigger
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.blocks.autotile.AutoTile
import me.weiwen.moromoro.serializers.ItemStackSerializer
import org.bukkit.inventory.ItemStack

@Serializable
sealed class BlockTemplate {
    abstract fun place(ctx: Context): Boolean

    @SerialName("sit-height")
    abstract val sitHeight: Double?
    @SerialName("sit-rotate")
    abstract val sitRotate: Boolean?

    abstract val drops: List<ItemStack>?
    abstract val experience: Int

    @SerialName("can-fortune")
    abstract val canFortune: Boolean

    abstract val hardness: Double
    abstract val tools: List<ItemStack>?

    abstract val sounds: SoundGroup

    abstract val triggers: Map<BlockTrigger, List<Action>>
}

@Serializable
data class SoundGroup(
    val `break`: Sound? = null,
    val fall: Sound? = null,
    val hit: Sound? = null,
    val place: Sound? = null,
    val step: Sound? = null,
)

@Serializable
data class Sound(
    val sound: String,
    val volume: Float,
    val pitch: Float,
)
