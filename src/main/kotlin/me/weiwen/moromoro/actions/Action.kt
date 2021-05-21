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
    var block: Block?,
    var blockFace: BlockFace?,
) {
    var isCancelled = false
    var removeItem = false
}

interface Action {
    fun perform(ctx: Context): Boolean
}

val actionModule = SerializersModule {
    polymorphic(Action::class) {
        subclass(ActionBar::class)
        subclass(AddPermanentPotionEffect::class)
        subclass(AddPotionEffect::class)
        subclass(AddVelocity::class)
        subclass(All::class)
        subclass(AllPlayers::class)
        subclass(Any::class)
        subclass(BiomeWand::class)
        subclass(BreakBlock::class)
        subclass(BuildersWand::class)
        subclass(Cancel::class)
        subclass(CanBuild::class)
        subclass(ConsoleCommand::class)
        subclass(Delay::class)
        subclass(Disguise::class)
        subclass(EquipItem::class)
        subclass(ExperienceBoost::class)
        subclass(Feed::class)
        subclass(FlyInClaims::class)
        subclass(Heal::class)
        subclass(If::class)
        subclass(IsFlying::class)
        subclass(IsInWorld::class)
        subclass(IsOnGround::class)
        subclass(IsSneaking::class)
        subclass(IsSprinting::class)
        subclass(ItemCooldown::class)
        // subclass(LaunchEntity::class)
        // subclass(LaunchFallingBlock::class)
        // subclass(LavaBucket::class)
        // subclass(MeasureDistance::class)
        subclass(Message::class)
        subclass(MultiTool::class)
        subclass(Noop::class)
        subclass(Not::class)
        subclass(PaintBrushPick::class)
        subclass(PaintBrushPaint::class)
        subclass(PathBlock::class)
        subclass(PlaceBlock::class)
        // subclass(PlaceRandomBlock::class)
        subclass(PlayerCommand::class)
        subclass(PlaySound::class)
        subclass(RemoveItem::class)
        subclass(RemovePermanentPotionEffect::class)
        // subclass(Repeat::class)
        // subclass(Rotate::class)
        subclass(StripBlock::class)
        subclass(SetVelocity::class)
        subclass(SpawnParticle::class)
        subclass(SudoCommand::class)
        subclass(TillBlock::class)
        // subclass(ToggleEnchantmentAction::class)
        // subclass(ToggleItemFrameVisibility::class)
        subclass(Undisguise::class)
        // subclass(WaterBucket::class)
    }
}
