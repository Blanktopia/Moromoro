@file:UseSerializers(MaterialSerializer::class, EnchantmentSerializer::class)

package me.weiwen.moromoro.actions

import kotlinx.serialization.modules.*
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.serializers.EnchantmentSerializer
import me.weiwen.moromoro.serializers.MaterialSerializer
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack

class Context(
    val event: Event?,
    val player: Player,
    val item: ItemStack,
    val entity: Entity?,
    val block: Block?,
    val blockFace: BlockFace?,
) {
    var isCancelled = false
}

interface Action {
    fun perform(ctx: Context): Boolean
}

val actionModule = SerializersModule {
    polymorphic(Action::class) {
        subclass(ActionBar::class)
        subclass(AddPermanentPotionEffect::class)
        subclass(AddPotionEffectAction::class)
        subclass(All::class)
        subclass(AllPlayers::class)
        subclass(Any::class)
        subclass(BiomeWand::class)
        subclass(BreakBlock::class)
        subclass(CanBuild::class)
        subclass(If::class)
        subclass(IsFlying::class)
        subclass(IsInWorld::class)
        subclass(IsOnGround::class)
        subclass(IsSneaking::class)
        subclass(IsSprinting::class)
        subclass(ItemCooldown::class)
        subclass(MultiTool::class)
        subclass(Noop::class)
        subclass(Not::class)
        subclass(PlaySound::class)
        subclass(RemovePermanentPotionEffect::class)

        default { Noop.serializer() }
    }
}
