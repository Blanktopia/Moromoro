@file:UseSerializers(EnchantmentSerializer::class)

package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.extensions.playSoundAt
import me.weiwen.moromoro.extensions.toRomanNumerals
import me.weiwen.moromoro.serializers.EnchantmentSerializer
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.meta.ItemMeta

@Serializable
@SerialName("toggle-enchantment")
data class ToggleEnchantment(val enchantment: Enchantment, val level: Int, val name: String) : Action {
    override fun perform(ctx: Context): Boolean {
        val item = ctx.item ?: return false
        val player = ctx.player ?: return false

        if (item.enchantments.containsKey(enchantment)) {
            item.removeEnchantment(enchantment)

            if (enchantment.key.namespace != "minecraft") {
                val itemMeta = item.itemMeta as ItemMeta

                itemMeta.lore = itemMeta.lore?.filter {
                    it.startsWith("${ChatColor.RESET}${ChatColor.GRAY}${enchantment.name}")
                            || it.startsWith("${ChatColor.GRAY}${enchantment.name}")
                }

                item.itemMeta = itemMeta
            }

            player.sendActionBar("${ChatColor.RED}Disabled ${name}.")
        } else {
            item.addEnchantment(enchantment, level)

            if (enchantment.key.namespace != "minecraft") {
                val itemMeta = item.itemMeta as ItemMeta

                val lore = StringBuilder().apply {
                    append(ChatColor.GRAY)
                    append(enchantment.name)
                    if (enchantment.maxLevel != 1) {
                        append(" ")
                        append(level.toRomanNumerals())
                    }
                }.toString()

                itemMeta.lore = itemMeta.lore?.apply { add(0, lore) }

                item.itemMeta = itemMeta
            }

            player.sendActionBar("${ChatColor.GREEN}Enabled ${name}.")
        }

        player.playSoundAt(Sound.ITEM_ARMOR_EQUIP_NETHERITE, SoundCategory.PLAYERS, 1.0f, 1.0f)

        return true
    }
}

