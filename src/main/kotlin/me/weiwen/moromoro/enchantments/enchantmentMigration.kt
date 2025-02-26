package me.weiwen.moromoro.enchantments

import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.TypedKey
import me.weiwen.moromoro.Moromoro.Companion.plugin
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Registry
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

val loreToEnchantment = mapOf(
    "Beheading" to "beheading",
    "Final" to "final",
    "Frost" to "frost",
    "Harvest" to "harvest",
    "Night Vision" to "night-vision",
    "Parry" to "parry",
    "Rush" to "rush",
    "Smelt" to "smelt",
    "Sniper" to "sniper",
    "Soulbound" to "soulbound",
    "Spectral" to "spectral",
    "Spring" to "spring",
    "Sting" to "sting",
    "Stride" to "stride",
)

val enchantmentLores = loreToEnchantment.keys.map {
    Component.text(it).color(NamedTextColor.GRAY).decorations(
        mapOf(
            TextDecoration.BOLD to TextDecoration.State.FALSE,
            TextDecoration.ITALIC to TextDecoration.State.FALSE,
            TextDecoration.OBFUSCATED to TextDecoration.State.FALSE,
            TextDecoration.STRIKETHROUGH to TextDecoration.State.FALSE,
            TextDecoration.UNDERLINED to TextDecoration.State.FALSE
        )
    )
}

fun migrateEnchantments(item: ItemStack): ItemStack? {
    if (item.lore() == null) return null

    // Migrate lore-based enchantments
    var migrated = false
    var item = item
    val lore = mutableListOf<Component>()
    for (line in item.lore()!!) {
        val migratedItem = migrateEnchantment(item, line)
        if (migratedItem == null) {
            lore.add(line)
        } else {
            item = migratedItem
            migrated = true
        }
    }
    item.lore(lore.ifEmpty { null })
    return if (migrated) item else null
}

fun migrateEnchantment(item: ItemStack, line: Component): ItemStack? {
    val (enchantment, level) = parseEnchantment(line) ?: return null
    item.addEnchant(enchantment, level, true)
    return item
}

fun parseEnchantment(line: Component): Pair<Enchantment, Int>? {
    val component = line.compact() as? TextComponent ?: return null
    val text = component.content()

    if (text.isEmpty()) return null

    val splitIndex = text.indexOfLast { it == ' ' }

    val level = if (splitIndex == -1) null else parseRomanNumerals(text.substring(splitIndex + 1))
    val name = if (level == null) text else text.substring(0, splitIndex)

    val key = loreToEnchantment[name] ?: return null
    val enchantment = Registry.ENCHANTMENT.get(TypedKey.create(RegistryKey.ENCHANTMENT, "${plugin.config.namespace}:${key}")) ?: return null

    return enchantment to (level ?: 1)
}

fun parseRomanNumerals(s: String): Int? {
    if (s.isEmpty()) return null

    val romanMap = mapOf('I' to 1, 'V' to 5, 'X' to 10, 'L' to 50, 'C' to 100, 'D' to 500, 'M' to 1000)
    var sum = 0
    var prevValue = romanMap[s[0]] ?: return null

    for (i in 1 until s.length) {
        val currentValue = romanMap[s[i]] ?: return null
        sum += if (currentValue > prevValue) {
            -prevValue
        } else {
            prevValue
        }
        prevValue = currentValue
    }
    sum += prevValue

    return sum
}