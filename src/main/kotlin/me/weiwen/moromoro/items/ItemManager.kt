@file:UseSerializers(
    MaterialSerializer::class,
    EnchantmentSerializer::class,
    ColorSerializer::class,
    UUIDSerializer::class
)

package me.weiwen.moromoro.managers

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Trigger
import me.weiwen.moromoro.actions.actionModule
import me.weiwen.moromoro.blocks.BlockTemplate
import me.weiwen.moromoro.extensions.customItemKey
import me.weiwen.moromoro.extensions.setHeadUrl
import me.weiwen.moromoro.extensions.toRomanNumerals
import me.weiwen.moromoro.serializers.*
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.persistence.PersistentDataType
import java.io.File
import java.nio.ByteBuffer
import java.util.*
import java.util.logging.Level

enum class CustomEquipmentSlot {
    HAND, OFF_HAND, FEET, LEGS, CHEST, HEAD, TRINKET
}

val EquipmentSlot.customEquipmentSlot: CustomEquipmentSlot
    get() = when (this) {
        EquipmentSlot.HAND -> CustomEquipmentSlot.HAND
        EquipmentSlot.OFF_HAND -> CustomEquipmentSlot.OFF_HAND
        EquipmentSlot.FEET -> CustomEquipmentSlot.FEET
        EquipmentSlot.LEGS -> CustomEquipmentSlot.LEGS
        EquipmentSlot.CHEST -> CustomEquipmentSlot.CHEST
        EquipmentSlot.HEAD -> CustomEquipmentSlot.HEAD
    }

val CustomEquipmentSlot.equipmentSlot: EquipmentSlot?
    get() = when (this) {
        CustomEquipmentSlot.HAND -> EquipmentSlot.HAND
        CustomEquipmentSlot.OFF_HAND -> EquipmentSlot.OFF_HAND
        CustomEquipmentSlot.FEET -> EquipmentSlot.FEET
        CustomEquipmentSlot.LEGS -> EquipmentSlot.LEGS
        CustomEquipmentSlot.CHEST -> EquipmentSlot.CHEST
        CustomEquipmentSlot.HEAD -> EquipmentSlot.HEAD
        else -> null
    }

@Serializable
data class ItemTemplate(
    val version: Int = 0,
    val alias: String? = null,
    val material: Material,
    val name: FormattedString? = null,
    val lore: FormattedString? = null,
    @SerialName("custom-model-data")
    val customModelData: Int? = null,
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

@Serializable
data class AttributeModifier(
    val attribute: Attribute,
    val uuid: UUID,
    val name: String,
    val amount: Double,
    val operation: org.bukkit.attribute.AttributeModifier.Operation,
    val slot: EquipmentSlot? = null,
)

val AttributeModifier.modifier: org.bukkit.attribute.AttributeModifier
    get() = org.bukkit.attribute.AttributeModifier(uuid, name, amount, operation, slot)

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


class ItemManager(val plugin: Moromoro) {
    var keys: Set<String> = setOf()
        private set
    var templates: Map<String, ItemTemplate> = mapOf()
        private set
    var triggers: MutableMap<String, Map<Trigger, List<Action>>> = mutableMapOf()
        private set

    fun enable() {
        load()
    }

    fun disable() {}

    fun load() {
        val directory = File(plugin.dataFolder, "items")

        if (!directory.isDirectory) {
            directory.mkdirs()
        }

        // We walk bottom up so that the files closer to the root are processed last, and will take priority.
        val files = directory.walkBottomUp().filter { file -> file.extension in setOf("json", "yml", "yaml") }

        triggers.clear()

        templates = files
            .mapNotNull { file -> parse(file)?.let { Pair(file.nameWithoutExtension, it) } }
            .associate { it }

        keys = templates.keys
    }

    private val json = Json {
        serializersModule = actionModule
    }

    private val yaml = Yaml(
        actionModule,
        YamlConfiguration(
            polymorphismStyle = PolymorphismStyle.Property
        )
    )

    private fun parse(file: File): ItemTemplate? {
        val key = file.nameWithoutExtension

        val format = when (file.extension) {
            "json" -> json
            "yml", "yaml" -> yaml
            else -> return null
        }

        val text = file.readText()
        val template = try {
            format.decodeFromString<ItemTemplate>(text)
        } catch (e: Exception) {
            plugin.logger.log(Level.SEVERE, "Error parsing '${file.name}': ${e.message}")
            return null
        }

        this.triggers[key] = template.triggers

        return template
    }

    fun migrateItem(item: ItemStack): ItemStack? {
        val key = item.customItemKey ?: return null
        val template = templates[key] ?: return null

        // Migrate aliased items
        val alias = template.alias
        if (alias != null) {
            return template.item(alias, item.amount)
        }

        // Migrate version
        val version = item.itemMeta.persistentDataContainer.get(
            NamespacedKey(plugin.config.namespace, "version"),
            PersistentDataType.INTEGER
        )
        if (version == 0 || (version ?: 0) != template.version || plugin.config.forceMigration) {
            return template.item(key, item.amount)
        }

        return null
    }

    fun creativeItemPicker(player: Player) {
        val gui = ChestGui(6, "Moromoro")

        val pages = PaginatedPane(0, 0, 8, 6)
        pages.populateWithItemStacks(templates.entries.map { (key, template) -> template.item(key) })
        pages.setOnClick {
            it.isCancelled = true
            val key = it.currentItem?.customItemKey ?: return@setOnClick
            val template = templates[key] ?: return@setOnClick
            player.inventory.addItem(
                template.item(
                    key, if (it.isShiftClick) {
                        64
                    } else {
                        1
                    }
                )
            )
        }
        gui.addPane(pages)

        val background = OutlinePane(8, 0, 1, 6)
        background.addItem(GuiItem(ItemStack(Material.BLACK_STAINED_GLASS_PANE)))
        background.setRepeat(true)
        background.priority = Pane.Priority.LOWEST
        background.setOnClick { it.isCancelled = true }
        gui.addPane(background)

        val navigation = StaticPane(8, 4, 1, 2)
        navigation.addItem(GuiItem(ItemStack(Material.RED_WOOL)) {
            if (pages.page > 0) {
                pages.page = pages.page - 1
                gui.update()
            }
            it.isCancelled = true
        }, 0, 0)
        navigation.addItem(GuiItem(ItemStack(Material.GREEN_WOOL)) {
            if (pages.page < pages.pages - 1) {
                pages.page = pages.page + 1
                gui.update()
            }
            it.isCancelled = true
        }, 0, 1)
        gui.addPane(navigation)


        gui.show(player)
    }
}