@file:UseSerializers(MaterialSerializer::class, EnchantmentSerializer::class)

package me.weiwen.moromoro.actions

import kotlinx.serialization.UseSerializers
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import me.weiwen.moromoro.actions.marker.*
import me.weiwen.moromoro.serializers.EnchantmentSerializer
import me.weiwen.moromoro.serializers.MaterialSerializer
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack

class Context(
    val event: Event?,
    val player: Player?,
    val item: ItemStack?,
    var entity: Entity?,
    var block: Block?,
    var blockFace: BlockFace?,
    var projectile: Projectile? = null,
) {
    var isCancelled = false
    var removeItem = false
}

interface Action {
    fun perform(ctx: Context): Boolean
}

val actionModule = SerializersModule {
    polymorphic(Action::class) {
        subclass(EntityHasMarker::class)
        subclass(MarkEntity::class)
        subclass(UnmarkEntity::class)
        subclass(ProjectileHasMarker::class)
        subclass(MarkProjectile::class)
        subclass(UnmarkProjectile::class)

        subclass(ActionBar::class)
        subclass(AddPermanentPotionEffect::class)
        subclass(AddPermission::class)
        subclass(AddPotionEffect::class)
        subclass(AddVelocity::class)
        subclass(All::class)
        subclass(AllPlayers::class)
        subclass(Any::class)
        subclass(BiomeWand::class)
        subclass(BlockIs::class)
        subclass(BreakBlock::class)
        subclass(BuildersWand::class)
        subclass(BuildersWandHighlight::class)
        subclass(CanBuild::class)
        subclass(Cancel::class)
        subclass(ClearHighlight::class)
        subclass(ConsoleCommand::class)
        subclass(ConsumeHunger::class)
        subclass(Delay::class)
        subclass(Disguise::class)
        subclass(EntityIs::class)
        subclass(EquipItem::class)
        subclass(ExperienceBoost::class)
        subclass(Feed::class)
        subclass(FlyInClaims::class)
        subclass(Heal::class)
        subclass(HighlightBlock::class)
        subclass(If::class)
        subclass(Immunity::class)
        subclass(IsFlying::class)
        subclass(IsInWorld::class)
        subclass(IsOnGround::class)
        subclass(IsSneaking::class)
        subclass(IsSprinting::class)
        subclass(ItemCooldown::class)
        subclass(ItemIs::class)
        subclass(LaunchEntity::class)
        subclass(LaunchItemProjectile::class)
        subclass(LaunchFallingBlock::class)
        subclass(LavaBucket::class)
        subclass(Light::class)
        subclass(Lightning::class)
        subclass(MeasureDistance::class)
        subclass(Message::class)
        subclass(MultiTool::class)
        subclass(Noop::class)
        subclass(Not::class)
        subclass(PaintBrushPaint::class)
        subclass(PaintBrushPick::class)
        subclass(PathBlock::class)
        subclass(PlaceBlock::class)
        subclass(PlaceRandomBlock::class)
        subclass(PlaySound::class)
        subclass(PlayerCommand::class)
        subclass(Raycast::class)
        subclass(RemoveItem::class)
        subclass(RemoveLight::class)
        subclass(RemovePermanentPotionEffect::class)
        subclass(RemovePermission::class)
        subclass(Repeat::class)
        subclass(Rotate::class)
        subclass(SetBlock::class)
        subclass(SetVelocity::class)
        subclass(Sleep::class)
        subclass(SpawnParticle::class)
        subclass(StripBlock::class)
        subclass(SudoCommand::class)
        subclass(TillBlock::class)
        subclass(ToggleEnchantment::class)
        subclass(ToggleItemFrameVisibility::class)
        subclass(Undisguise::class)
        subclass(UsePlayerFacing::class)
        subclass(WaterBucket::class)
    }
}
