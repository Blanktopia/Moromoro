package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Trigger {
    @SerialName("damage-entity") DAMAGE_ENTITY,

    @SerialName("left-click") LEFT_CLICK,
    @SerialName("left-click-block") LEFT_CLICK_BLOCK,
    @SerialName("left-click-air") LEFT_CLICK_AIR,

    @SerialName("right-click") RIGHT_CLICK,
    @SerialName("right-click-block") RIGHT_CLICK_BLOCK,
    @SerialName("right-click-air") RIGHT_CLICK_AIR,
    @SerialName("right-click-entity") RIGHT_CLICK_ENTITY,

    @SerialName("equip-armor") EQUIP_ARMOR,
    @SerialName("unequip-armor") UNEQUIP_ARMOR,
    @SerialName("equip-head") EQUIP_HEAD,
    @SerialName("equip-chest") EQUIP_CHEST,
    @SerialName("equip-legs") EQUIP_LEGS,
    @SerialName("equip-feet") EQUIP_FEET,

    @SerialName("consume") CONSUME,

    @SerialName("break-block") BREAK_BLOCK,
    @SerialName("place-block") PLACE_BLOCK,

    @SerialName("drop") DROP,

    @SerialName("move") MOVE,
    @SerialName("jump") JUMP,
    @SerialName("sneak") SNEAK,
    @SerialName("unsneak") UNSNEAK,
    @SerialName("sprint") SPRINT,
    @SerialName("unsprint") UNSPRINT,
    @SerialName("fly") FLY,
    @SerialName("unfly") UNFLY,
    @SerialName("glide") GLIDE,
    @SerialName("unglide") UNGLIDE,
    @SerialName("swim") SWIM,
    @SerialName("unswim") UNSWIM
    ;
}

val EQUIPPED_TRIGGERS = setOf(
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
    Trigger.UNSWIM
)
