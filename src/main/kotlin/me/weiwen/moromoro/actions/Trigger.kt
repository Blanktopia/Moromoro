package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Trigger {
    @SerialName("damage-entity")
    DAMAGE_ENTITY,

    @SerialName("left-click")
    LEFT_CLICK,
    @SerialName("left-click-block")
    LEFT_CLICK_BLOCK,
    @SerialName("left-click-air")
    LEFT_CLICK_AIR,

    @SerialName("right-click")
    RIGHT_CLICK,
    @SerialName("right-click-block")
    RIGHT_CLICK_BLOCK,
    @SerialName("right-click-air")
    RIGHT_CLICK_AIR,
    @SerialName("right-click-entity")
    RIGHT_CLICK_ENTITY,

    @SerialName("equip-armor")
    EQUIP_ARMOR,
    @SerialName("unequip-armor")
    UNEQUIP_ARMOR,
    @SerialName("equip-head")
    EQUIP_HEAD,
    @SerialName("equip-chest")
    EQUIP_CHEST,
    @SerialName("equip-legs")
    EQUIP_LEGS,
    @SerialName("equip-feet")
    EQUIP_FEET,

    @SerialName("fishing")
    FISHING,
    @SerialName("fish-caught-fish")
    FISH_CAUGHT_FISH,
    @SerialName("fish-caught-entity")
    FISH_CAUGHT_ENTITY,
    @SerialName("fish-in-ground")
    FISH_IN_GROUND,
    @SerialName("fish-failed-attempt")
    FISH_FAILED_ATTEMPT,
    @SerialName("fish-reel-in")
    FISH_REEL_IN,
    @SerialName("fish-bite")
    FISH_BITE,

    @SerialName("consume")
    CONSUME,

    @SerialName("break-block")
    BREAK_BLOCK,
    @SerialName("place-block")
    PLACE_BLOCK,

    @SerialName("projectile-launch")
    PROJECTILE_LAUNCH,
    @SerialName("projectile-hit")
    PROJECTILE_HIT,
    @SerialName("projectile-tick")
    PROJECTILE_TICK,

    @SerialName("drop")
    DROP,
    @SerialName("swap-hand")
    SWAP_HAND,

    @SerialName("tick")
    TICK,
    @SerialName("tick-slow")
    TICK_SLOW,

    @SerialName("move")
    MOVE,
    @SerialName("jump")
    JUMP,
    @SerialName("sneak")
    SNEAK,
    @SerialName("unsneak")
    UNSNEAK,
    @SerialName("sprint")
    SPRINT,
    @SerialName("unsprint")
    UNSPRINT,
    @SerialName("fly")
    FLY,
    @SerialName("unfly")
    UNFLY,
    @SerialName("glide")
    GLIDE,
    @SerialName("unglide")
    UNGLIDE,
    @SerialName("swim")
    SWIM,
    @SerialName("unswim")
    UNSWIM,
    @SerialName("damaged")
    DAMAGED,

    @SerialName("right-click-inventory")
    RIGHT_CLICK_INVENTORY,
    @SerialName("left-click-inventory")
    LEFT_CLICK_INVENTORY,
    @SerialName("middle-click-inventory")
    MIDDLE_CLICK_INVENTORY,
    @SerialName("shift-right-click-inventory")
    SHIFT_RIGHT_CLICK_INVENTORY,
    @SerialName("shift-left-click-inventory")
    SHIFT_LEFT_CLICK_INVENTORY,
    @SerialName("double-click-inventory")
    DOUBLE_CLICK_INVENTORY,
    @SerialName("drop-inventory")
    DROP_INVENTORY,
    @SerialName("control-drop-inventory")
    CONTROL_DROP_INVENTORY,
    @SerialName("left-border-inventory")
    LEFT_BORDER_INVENTORY,
    @SerialName("right-border-inventory")
    RIGHT_BORDER_INVENTORY,
    @SerialName("number-1-inventory")
    NUMBER_1_INVENTORY,
    @SerialName("number-2-inventory")
    NUMBER_2_INVENTORY,
    @SerialName("number-3-inventory")
    NUMBER_3_INVENTORY,
    @SerialName("number-4-inventory")
    NUMBER_4_INVENTORY,
    @SerialName("number-5-inventory")
    NUMBER_5_INVENTORY,
    @SerialName("number-6-inventory")
    NUMBER_6_INVENTORY,
    @SerialName("number-7-inventory")
    NUMBER_7_INVENTORY,
    @SerialName("number-8-inventory")
    NUMBER_8_INVENTORY,
    @SerialName("number-9-inventory")
    NUMBER_9_INVENTORY,
    @SerialName("creative-inventory")
    CREATIVE_INVENTORY,
    @SerialName("swap-offhand-inventory")
    SWAP_OFFHAND_INVENTORY,
    ;
}

val EQUIPPED_TRIGGERS = setOf(
    Trigger.TICK,
    Trigger.MOVE,
    Trigger.JUMP,
    Trigger.SNEAK,
    Trigger.UNSNEAK,
    Trigger.SPRINT,
    Trigger.UNSPRINT,
    Trigger.FLY,
    Trigger.UNFLY,
    Trigger.GLIDE,
    Trigger.UNGLIDE,
    Trigger.SWIM,
    Trigger.UNSWIM,
    Trigger.DAMAGED,
    Trigger.DROP,
    Trigger.SWAP_HAND,
)
