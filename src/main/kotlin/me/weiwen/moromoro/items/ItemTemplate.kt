@file:UseSerializers(
    ItemTypeSerializer::class,
    EnchantmentSerializer::class,
    ColorSerializer::class,
)

package me.weiwen.moromoro.items

import io.papermc.paper.datacomponent.DataComponentTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Trigger
import me.weiwen.moromoro.blocks.BlockTemplate
import me.weiwen.moromoro.extensions.setHeadUrl
import me.weiwen.moromoro.extensions.toRomanNumerals
import me.weiwen.moromoro.resourcepack.ItemModel
import me.weiwen.moromoro.serializers.*
import me.weiwen.moromoro.types.AttributeModifier
import me.weiwen.moromoro.types.CustomEquipmentSlot
import me.weiwen.moromoro.types.modifier
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Color
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ItemType
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

    val name: FormattedString? = null,
    val lore: List<FormattedString>? = null,

    val item: ItemType,
    @SerialName("custom-model-data")
    val customModelData: Int? = null,
    val head: String? = null,

    val model: String? = null,
    val models: List<ItemModel> = listOf(),

    val unique: Boolean = false,
    val unbreakable: Boolean = false,
    val soulbound: Boolean = false,
    val unenchantable: Boolean = false,

    val enchantments: Map<Enchantment, Int> = mapOf(),
    val attributes: List<AttributeModifier> = listOf(),
    val flags: List<ItemFlag> = listOf(),

    val color: Color? = null,
    val dyeable: Boolean = false,

    val triggers: Map<Trigger, List<Action>> = mapOf(),
    val slots: Set<CustomEquipmentSlot> = setOf(),

    val block: BlockTemplate? = null,
)

fun ItemTemplate.item(key: String, amount: Int = 1): ItemStack {
    val item = this.item.createItemStack(amount)

    head?.let { item.setHeadUrl(name?.text ?: "", it) }

    val itemMeta = item.itemMeta as ItemMeta

    name?.let { itemMeta.displayName(it.component.decoration(TextDecoration.ITALIC, false)) }
    lore?.let { itemMeta.lore(it.map { text -> text.component.decoration(TextDecoration.ITALIC, false) }) }

    itemMeta.isUnbreakable = unbreakable

    customModelData?.let { data -> itemMeta.setCustomModelData(data) }

    if (model != null) {
        item.setData(DataComponentTypes.ITEM_MODEL, Key.key(model))
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

    if (soulbound) {
        persistentData.set(
            NamespacedKey(Moromoro.plugin.config.namespace, "soulbound"),
            PersistentDataType.BYTE,
            1
        )
        itemMeta.lore()?.let {
            it.add(0, Component.text("Soulbound")
                    .color(TextColor.color(0xAAAAAA))
                    .decoration(TextDecoration.ITALIC, false))
            itemMeta.lore(it)
        }
    }

    if (unenchantable) {
        persistentData.set(
            NamespacedKey(Moromoro.plugin.config.namespace, "unenchantable"),
            PersistentDataType.BYTE,
            1
        )
    }

    enchantments.forEach { (enchant, level) ->
        itemMeta.addEnchant(enchant, level, true)
        if (enchant.key.namespace != "minecraft") {
            val name = StringBuilder().apply {
                append(enchant.name)
                if (enchant.maxLevel != 1) {
                    append(" ")
                    append(level.toRomanNumerals())
                }
            }.toString()

            itemMeta.lore()?.let {
                it.add(0, Component.text(name)
                    .color(TextColor.color(0xAAAAAA))
                    .decoration(TextDecoration.ITALIC, false))
                itemMeta.lore(it)
            }
        }
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


