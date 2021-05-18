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

    @SerialName("MOVE") MOVE,
    @SerialName("JUMP") JUMP,
    @SerialName("SNEAK") SNEAK,
    @SerialName("UNSNEAK") UNSNEAK,
    @SerialName("SPRINT") SPRINT,
    @SerialName("UNSPRINT") UNSPRINT,
    @SerialName("FLY") FLY,
    @SerialName("UNFLY") UNFLY,
    @SerialName("GLIDE") GLIDE,
    @SerialName("UNGLIDE") UNGLIDE,
    @SerialName("SWIM") SWIM,
    @SerialName("UNSWIM") UNSWIM
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
