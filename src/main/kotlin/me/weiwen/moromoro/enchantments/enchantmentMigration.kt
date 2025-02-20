package me.weiwen.moromoro.enchantments

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration

val enchantmentLores = setOf(
    "Beheading",
    "Final",
    "Frost",
    "Harvest",
    "Night Vision",
    "Parry",
    "Rush",
    "Smelt",
    "Sniper",
    "Soulbound",
    "Spectral",
    "Spring",
    "Sting",
    "Stride",
).map {
    Component.text(it).decorations(
        mapOf(
            TextDecoration.BOLD to TextDecoration.State.FALSE,
            TextDecoration.ITALIC to TextDecoration.State.FALSE,
            TextDecoration.OBFUSCATED to TextDecoration.State.FALSE,
            TextDecoration.STRIKETHROUGH to TextDecoration.State.FALSE,
            TextDecoration.UNDERLINED to TextDecoration.State.FALSE
        )
    )
}