@file:UseSerializers(
    KeySerializer::class,
    ItemTypeSerializer::class,
    EnchantmentSerializer::class,
    ColorSerializer::class,
)

package me.weiwen.moromoro.items

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.CustomModelData
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers
import io.papermc.paper.datacomponent.item.UseCooldown
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.Moromoro.Companion.plugin
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Trigger
import me.weiwen.moromoro.blocks.BlockTemplate
import me.weiwen.moromoro.extensions.setHeadUrl
import me.weiwen.moromoro.serializers.*
import me.weiwen.moromoro.types.AttributeModifier
import me.weiwen.moromoro.types.CustomEquipmentSlot
import me.weiwen.moromoro.types.modifier
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemRarity
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.persistence.PersistentDataType
import java.util.logging.Level

@Serializable
data class ItemTemplate(
    val version: Int = 0,
    val alias: String? = null,

    val name: FormattedString? = null,
    val lore: List<FormattedString>? = null,

    val item: Key,
    @SerialName("item-model")
    val itemModel: Key? = null,
    @SerialName("custom-model-data")
    val customModelData: Double? = null,
    val head: String? = null,

    val model: Key? = null,
    @SerialName("custom-model")
    val customModel: String? = null,

    val unique: Boolean = false,
    val unbreakable: Boolean = false,
    val unenchantable: Boolean = false,

    val enchantments: Map<Enchantment, Int> = mapOf(),
    @SerialName("enchantment-glint")
    val enchantmentGlint: Boolean? = null,
    val attributes: List<AttributeModifier> = listOf(),
    val flags: List<ItemFlag> = listOf(),
    @SerialName("cooldown-group")
    val cooldownGroup: Key? = null,
    val tool: ToolTemplate? = null,
    val rarity: String? = null,
    @SerialName("max-stack-size")
    val maxStackSize: Int? = null,

    val color: Color? = null,
    val dyeable: Boolean = false,

    val triggers: Map<Trigger, List<Action>> = mapOf(),
    val slots: Set<CustomEquipmentSlot> = setOf(),

    val block: BlockTemplate? = null,
)

fun ItemTemplate.item(key: String, amount: Int = 1): ItemStack {
    val itemType = Registry.ITEM.get(this.item)
    if (itemType == null) {
        plugin.logger.severe("Invalid item key for item ${key}: ${this.item}")
        return ItemStack.of(Material.STICK, amount)
    }
    val item = itemType.createItemStack(amount)

    head?.let { item.setHeadUrl(name?.text ?: "", it) }

    name?.let { item.setData(DataComponentTypes.ITEM_NAME, it.component.decoration(TextDecoration.ITALIC, false)) }
    lore?.let { item.lore(it.map { text -> text.component.decoration(TextDecoration.ITALIC, false) }) }

    if (unbreakable) item.setData(DataComponentTypes.UNBREAKABLE)

    customModelData?.let { data -> item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addFloat(data.toFloat()).build()) }
    itemModel?.let { model -> item.setData(DataComponentTypes.ITEM_MODEL, model) }

    if (attributes.isNotEmpty()) {
        val attributeModifiers = ItemAttributeModifiers.itemAttributes()
        attributes.forEach { attributeModifiers.addModifier(it.attribute, it.modifier, it.slot) }
        item.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, attributeModifiers.build())
    }

    enchantmentGlint?.let { glint -> item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, glint) }
    cooldownGroup?.let { group -> item.setData(DataComponentTypes.USE_COOLDOWN, UseCooldown.useCooldown(1f).cooldownGroup(cooldownGroup).build()) }
    tool?.let { tool -> item.setData(DataComponentTypes.TOOL, tool.dataComponent()) }
    rarity?.let { rarity -> item.setData(DataComponentTypes.RARITY, ItemRarity.valueOf(rarity.uppercase()))}

    val stackSize = if (unique) { 1 } else { maxStackSize }
    stackSize?.let { maxStackSize -> item.setData(DataComponentTypes.MAX_STACK_SIZE, maxStackSize)}

    val itemMeta = item.itemMeta

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

    if (unenchantable) {
        persistentData.set(
            NamespacedKey(Moromoro.plugin.config.namespace, "unenchantable"),
            PersistentDataType.BYTE,
            1
        )
    }

    enchantments.forEach { (enchant, level) ->
        itemMeta.addEnchant(enchant, level, true)
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


