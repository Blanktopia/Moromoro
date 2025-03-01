package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class BlockTrigger {
    @SerialName("block-place")
    BLOCK_PLACE,
    @SerialName("block-break")
    BLOCK_BREAK,
    @SerialName("block-use")
    BLOCK_USE,
    ;
}
