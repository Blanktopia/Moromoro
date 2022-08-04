@file:UseSerializers(
    MaterialSerializer::class,
    EnchantmentSerializer::class,
    ColorSerializer::class,
)

package me.weiwen.moromoro.items

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Trigger
import me.weiwen.moromoro.blocks.BlockTemplate
import me.weiwen.moromoro.extensions.setHeadUrl
import me.weiwen.moromoro.extensions.toRomanNumerals
import me.weiwen.moromoro.serializers.ColorSerializer
import me.weiwen.moromoro.serializers.EnchantmentSerializer
import me.weiwen.moromoro.serializers.FormattedString
import me.weiwen.moromoro.serializers.MaterialSerializer
import me.weiwen.moromoro.types.AttributeModifier
import me.weiwen.moromoro.types.CustomEquipmentSlot
import me.weiwen.moromoro.types.modifier
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.persistence.PersistentDataType
import java.nio.ByteBuffer
import java.util.*
import java.util.logging.Level

@Serializable
data class ItemTemplate(
    val version: Int = 0,
    val alias: String? = null,
    val material: Material,
    val name: FormattedString? = null,
    val lore: FormattedString? = null,
    @SerialName("custom-model-data")
    val customModelData: Int? = null,
    val model: String? = null,
    val unique: Boolean = false,
    val head: String? = null,
    val unbreakable: Boolean = false,
    val enchantments: Map<Enchantment, Int> = mapOf(),
    val attributes: List<AttributeModifier> = listOf(),
    val flags: List<ItemFlag> = listOf(),
    val color: Color? = null,
    val dyeable: Boolean = false,
    val triggers: Map<Trigger, List<Action>> = mapOf(),
    val block: BlockTemplate? = null,
    val slots: Set<CustomEquipmentSlot> = setOf(),
)

fun ItemTemplate.item(key: String, amount: Int = 1): ItemStack {
    val item = ItemStack(material, amount)

    head?.let { item.setHeadUrl(name?.value ?: "", it) }

    val itemMeta = item.itemMeta as ItemMeta

    name?.let { itemMeta.setDisplayName(it.value) }
    lore?.let { itemMeta.lore = it.value.lines() }

    itemMeta.isUnbreakable = unbreakable

    customModelData?.let { data -> itemMeta.setCustomModelData(data) }

    enchantments.forEach { (enchant, level) ->
        itemMeta.addEnchant(enchant, level, true)
        if (enchant.key.namespace != "minecraft") {
            val lore = StringBuilder().apply {
                append(ChatColor.GRAY)
                append(enchant.name)
                if (enchant.maxLevel != 1) {
                    append(" ")
                    append(level.toRomanNumerals())
                }
            }.toString()

            itemMeta.lore = itemMeta.lore?.apply { add(0, lore) }
        }
    }

    attributes.forEach { itemMeta.addAttributeModifier(it.attribute, it.modifier) }

    flags.forEach { itemMeta.addItemFlags(it) }

    val persistentData = itemMeta.persistentDataContainer
    persistentData.set(NamespacedKey(Moromoro.plugin.config.namespace, "type"), PersistentDataType.STRING, key)
    if (version != 0) {
        persistentData.set(
            NamespacedKey(Moromoro.plugin.config.namespace, "version"),
            PersistentDataType.INTEGER,
            version
        )
    }

    if (unique) {
        val uuid = UUID.randomUUID()
        val bb = ByteBuffer.wrap(ByteArray(16))
        bb.putLong(uuid.mostSignificantBits)
        bb.putLong(uuid.leastSignificantBits)
        persistentData.set(
            NamespacedKey(Moromoro.plugin.config.namespace, "uuid"),
            PersistentDataType.BYTE_ARRAY,
            bb.array()
        )
    }

    color?.let {
        if (itemMeta !is LeatherArmorMeta) {
            Moromoro.plugin.logger.log(Level.WARNING, "color property is defined, but item is not colorable")
        } else {
            itemMeta.setColor(color)
        }
    }

    item.itemMeta = itemMeta

    return item
}


