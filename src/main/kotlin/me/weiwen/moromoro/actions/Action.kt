@file:UseSerializers(MaterialSerializer::class, EnchantmentSerializer::class)

package me.weiwen.moromoro.actions

import kotlinx.serialization.modules.*
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.modules.SerializersModuleBuilder
import me.weiwen.moromoro.serializers.EnchantmentSerializer
import me.weiwen.moromoro.serializers.MaterialSerializer
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack

data class Context(
    val event: Event,
    val player: Player,
    val item: ItemStack,
    val entity: Entity?,
    val block: Block?,
    val blockFace: BlockFace?,
)

interface Action {
    fun perform(ctx: Context): Boolean
}

val actionModule = SerializersModule {
    polymorphic(Action::class) {
        // Flow Control
        subclass(If::class)
        subclass(Noop::class)

        // Boolean Operations
        subclass(And::class)
        subclass(Or::class)

        // Conditions
        subclass(IsInWorld::class)
        subclass(CanBuild::class)

        // Actions
        subclass(MultiTool::class)
        subclass(Hammer::class)
        subclass(PlaySound::class)

        default { Noop.serializer() }
    }
}
