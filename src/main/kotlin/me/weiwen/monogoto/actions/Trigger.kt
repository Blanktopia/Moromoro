package me.weiwen.monogoto.actions

enum class Trigger {
    LEFT_CLICK_BLOCK,
    RIGHT_CLICK_BLOCK,
    LEFT_CLICK_ENTITY,
    RIGHT_CLICK_ENTITY,
    BREAK_BLOCK,
    PLACE_BLOCK;

    companion object {
        fun parse(s: String): Trigger {
            return when (s) {
                "left-click-block" -> LEFT_CLICK_BLOCK
                "right-click-block" -> RIGHT_CLICK_BLOCK
                "left-click-entity" -> LEFT_CLICK_ENTITY
                "right-click-entity" -> RIGHT_CLICK_ENTITY
                "break-block" -> BREAK_BLOCK
                "place-block" -> PLACE_BLOCK
                else -> throw EnumConstantNotPresentException(Trigger::class.java, s)
            }
        }
    }
}