package me.weiwen.moromoro.actions

import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import me.weiwen.moromoro.actions.block.*
import me.weiwen.moromoro.actions.clock.Delay
import me.weiwen.moromoro.actions.clock.Repeat
import me.weiwen.moromoro.actions.command.*
import me.weiwen.moromoro.actions.condition.*
import me.weiwen.moromoro.actions.customBlock.ToggleCustomBlock
import me.weiwen.moromoro.actions.damage.DamageEntity
import me.weiwen.moromoro.actions.damage.Immunity
import me.weiwen.moromoro.actions.damage.Knockback
import me.weiwen.moromoro.actions.damage.SetNoDamageTick
import me.weiwen.moromoro.actions.disguise.Disguise
import me.weiwen.moromoro.actions.disguise.Undisguise
import me.weiwen.moromoro.actions.effect.*
import me.weiwen.moromoro.actions.enchantment.ToggleEnchantment
import me.weiwen.moromoro.actions.event.Cancel
import me.weiwen.moromoro.actions.hunger.*
import me.weiwen.moromoro.actions.item.AttackCooldown
import me.weiwen.moromoro.actions.item.EquipItem
import me.weiwen.moromoro.actions.item.ItemCooldown
import me.weiwen.moromoro.actions.item.RemoveItem
import me.weiwen.moromoro.actions.logic.*
import me.weiwen.moromoro.actions.logic.Any
import me.weiwen.moromoro.actions.marker.*
import me.weiwen.moromoro.actions.mechanic.*
import me.weiwen.moromoro.actions.mechanic.blockzapper.ReplaceBlock
import me.weiwen.moromoro.actions.mechanic.blockzapper.SelectMaterial
import me.weiwen.moromoro.actions.mechanic.builderswand.BuildersWand
import me.weiwen.moromoro.actions.mechanic.builderswand.BuildersWandHighlight
import me.weiwen.moromoro.actions.mechanic.grapple.GrappleTick
import me.weiwen.moromoro.actions.mechanic.grapple.LaunchGrapple
import me.weiwen.moromoro.actions.mechanic.measuringtape.MeasureDistance
import me.weiwen.moromoro.actions.mechanic.measuringtape.MeasureDistanceTick
import me.weiwen.moromoro.actions.mechanic.paintbrush.PaintBrushHighlight
import me.weiwen.moromoro.actions.mechanic.paintbrush.PaintBrushPaint
import me.weiwen.moromoro.actions.mechanic.paintbrush.PaintBrushPick
import me.weiwen.moromoro.actions.messages.ActionBar
import me.weiwen.moromoro.actions.messages.Message
import me.weiwen.moromoro.actions.player.AddAttributeModifier
import me.weiwen.moromoro.actions.player.RemoveAttributeModifier
import me.weiwen.moromoro.actions.player.Sleep
import me.weiwen.moromoro.actions.potioneffect.AddPermanentPotionEffect
import me.weiwen.moromoro.actions.potioneffect.AddPotionEffect
import me.weiwen.moromoro.actions.potioneffect.RemovePermanentPotionEffect
import me.weiwen.moromoro.actions.projectile.*
import me.weiwen.moromoro.actions.selectors.*
import me.weiwen.moromoro.actions.velocity.AddVelocity
import me.weiwen.moromoro.actions.velocity.ClampVelocity
import me.weiwen.moromoro.actions.velocity.MultiplyVelocity
import me.weiwen.moromoro.actions.velocity.SetVelocity
import me.weiwen.moromoro.blocks.CustomBlock
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack

class Context(
    val event: Event?,
    var player: Player?,
    val item: ItemStack?,
    var entity: Entity?,
    var block: Block?,
    var blockFace: BlockFace?,
    var projectile: Projectile? = null,
    var customBlock: CustomBlock? = null,
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
        subclass(AddAttributeModifier::class)
        subclass(AddPermanentPotionEffect::class)
        subclass(AddPermission::class)
        subclass(AddPotionEffect::class)
        subclass(AddVelocity::class)
        subclass(All::class)
        subclass(AllPlayers::class)
        subclass(Any::class)
        subclass(ArrowColor::class)
        subclass(ArrowVolley::class)
        subclass(AttackCooldown::class)
        subclass(BiomeWand::class)
        subclass(BiomeWandPick::class)
        subclass(BlockIs::class)
        subclass(BreakBlock::class)
        subclass(BuildersWand::class)
        subclass(BuildersWandHighlight::class)
        subclass(CanBuild::class)
        subclass(Cancel::class)
        subclass(ClampVelocity::class)
        subclass(ClearHighlight::class)
        subclass(ConsoleCommand::class)
        subclass(ConsumeHunger::class)
        subclass(CycleFlora::class)
        subclass(DamageEntity::class)
        subclass(Delay::class)
        subclass(Disguise::class)
        subclass(EntityIs::class)
        subclass(EquipItem::class)
        subclass(Everstone::class)
        subclass(EvokerFang::class)
        subclass(ExperienceBoost::class)
        subclass(Explode::class)
        subclass(Feed::class)
        subclass(FlyInClaims::class)
        subclass(GrappleTick::class)
        subclass(Heal::class)
        subclass(HighlightBlock::class)
        subclass(Hunger::class)
        subclass(If::class)
        subclass(Ignite::class)
        subclass(Immunity::class)
        subclass(IsEntityDamageable::class)
        subclass(IsFlying::class)
        subclass(IsGliding::class)
        subclass(IsInWorld::class)
        subclass(IsKeyDown::class)
        subclass(IsOnGround::class)
        subclass(IsSneaking::class)
        subclass(IsSprinting::class)
        subclass(ItemCooldown::class)
        subclass(ItemIs::class)
        subclass(Knockback::class)
        subclass(LastPressedKeyWithin::class)
        subclass(LaunchEntity::class)
        subclass(LaunchFallingBlock::class)
        subclass(LaunchGrapple::class)
        subclass(LavaBucket::class)
        subclass(Light::class)
        subclass(Lightning::class)
        subclass(LightningSword::class)
        subclass(MeasureDistance::class)
        subclass(MeasureDistanceTick::class)
        subclass(Message::class)
        subclass(MultiTool::class)
        subclass(MultiplyVelocity::class)
        subclass(NearbyEntities::class)
        subclass(Noop::class)
        subclass(Not::class)
        subclass(NullPlayer::class)
        subclass(PackingTape::class)
        subclass(PaintBrushHighlight::class)
        subclass(PaintBrushPaint::class)
        subclass(PaintBrushPick::class)
        subclass(PathBlock::class)
        subclass(PlaceBlock::class)
        subclass(PlaceRandomBlock::class)
        subclass(PlaySound::class)
        subclass(PlayerCommand::class)
        subclass(Pokeball::class)
        subclass(ProjectileGravity::class)
        subclass(ProjectileGravity::class)
        subclass(Raycast::class)
        subclass(RemoveAttributeModifier::class)
        subclass(RemoveItem::class)
        subclass(RemoveLight::class)
        subclass(RemovePermanentPotionEffect::class)
        subclass(RemovePermission::class)
        subclass(RemoveProjectile::class)
        subclass(Repeat::class)
        subclass(ReplaceBlock::class)
        subclass(Rotate::class)
        subclass(Saturation::class)
        subclass(SelectMaterial::class)
        subclass(SetBlock::class)
        subclass(SetHunger::class)
        subclass(SetNoDamageTick::class)
        subclass(SetSaturatedRegenRate::class)
        subclass(SetSaturation::class)
        subclass(SetStarvationRate::class)
        subclass(SetUnsaturatedRegenRate::class)
        subclass(SetVelocity::class)
        subclass(Sleep::class)
        subclass(SpawnParticle::class)
        subclass(SpawnParticleLine::class)
        subclass(Sponge::class)
        subclass(StripBlock::class)
        subclass(SudoCommand::class)
        subclass(TillBlock::class)
        subclass(ToggleCustomBlock::class)
        subclass(ToggleEnchantment::class)
        subclass(ToggleItemFrameVisibility::class)
        subclass(Undisguise::class)
        subclass(UsePlayerFacing::class)
        subclass(VoidBundle::class)
        subclass(WaterBucket::class)
    }
}
