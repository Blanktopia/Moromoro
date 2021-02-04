package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Trigger {
    @SerialName("left-click-block") LEFT_CLICK_BLOCK,
    @SerialName("right-click-block") RIGHT_CLICK_BLOCK,
    @SerialName("left-click-entity") LEFT_CLICK_ENTITY,
    @SerialName("right-click-entity") RIGHT_CLICK_ENTITY,
    @SerialName("break-block") BREAK_BLOCK,
    @SerialName("place-block") PLACE_BLOCK;
}
