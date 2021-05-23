@file:UseSerializers(MaterialSerializer::class, EnchantmentSerializer::class, UUIDSerializer::class)

package me.weiwen.moromoro.managers

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.actions.Trigger
import me.weiwen.moromoro.actions.actionModule
import me.weiwen.moromoro.extensions.*
import me.weiwen.moromoro.serializers.EnchantmentSerializer
import me.weiwen.moromoro.serializers.FormattedString
import me.weiwen.moromoro.serializers.MaterialSerializer
import me.weiwen.moromoro.serializers.UUIDSerializer
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemFrame
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import java.io.File
import java.util.*
import java.util.logging.Level

@Serializable
data class ItemTemplate(
    val material: Material,
    val name: FormattedString? = null,
    val lore: FormattedString? = null,
    @SerialName("custom-model-data")
    val customModelData: Int? = null,
    val head: String? = null,
    val unbreakable: Boolean = false,
    val enchantments: Map<Enchantment, Int> = mapOf(),
    val attributes: List<AttributeModifier> = listOf(),
    val flags: List<ItemFlag> = listOf(),
    val triggers: Map<Trigger, List<Action>> = mapOf(),
    val block: BlockTemplate? = null,
)

@Serializable
data class AttributeModifier(
    val attribute: Attribute,
    val uuid: UUID,
    val name: String,
    val amount: Double,
    val operation: org.bukkit.attribute.AttributeModifier.Operation,
    val slot: EquipmentSlot,
)

@Serializable
sealed class BlockTemplate {
    abstract fun place(ctx: Context)
}

@Serializable
@SerialName("item")
/* Placed using invisible item frames */
class ItemBlockTemplate(val collision: Boolean) : BlockTemplate() {
    override fun place(ctx: Context) {
        val key = ctx.item.customItemKey ?: return

        val block = ctx.block ?: return
        val blockFace = ctx.blockFace ?: return

        val rotation = ctx.player.location.rotation

        val location = block.getRelative(blockFace).location
        val world = location.world ?: return

        // Try to place item frame
        val itemFrame = try {
            world.spawnEntity(location, EntityType.ITEM_FRAME) as ItemFrame
        } catch (e: java.lang.IllegalArgumentException) {
            return
        }.apply {
            setFacingDirection(blockFace, true)
            setRotation(rotation)
            isFixed = true
            isVisible = false

            // Set persistent data
            persistentDataContainer.set(
                NamespacedKey(Moromoro.plugin.config.namespace, "key"),
                PersistentDataType.STRING,
                key
            )
        }

        // Move item into item frame
        val item = ctx.item.clone()
        item.itemMeta = item.itemMeta.apply {
            setDisplayName(null)
        }
        item.amount = 1
        ctx.item.amount -= 1
        itemFrame.setItem(item, false)

        if (collision) {
            location.block.type = Material.BARRIER
        }

        world.playSound(location, Sound.BLOCK_WOOD_PLACE, 1.0f, 1.0f)
    }
}

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
            Moromoro.plugin.logger.log(Level.INFO, "$lore ${itemMeta.lore?.joinToString("")}")
        }
    }

    attributes.forEach { itemMeta.addAttributeModifier(it.attribute, it.modifier) }

    flags.forEach { itemMeta.addItemFlags(it) }

    val data = itemMeta.persistentDataContainer
    data.set(NamespacedKey(Moromoro.plugin.config.namespace, "type"), PersistentDataType.STRING, key)

    item.itemMeta = itemMeta

    return item
}


class ItemManager(val plugin: Moromoro) {
    var keys: Set<String> = setOf()
        private set
    var templates: Map<String, ItemTemplate> = mapOf()
        private set
    var blockTemplates: Map<String, BlockTemplate> = mapOf()
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
        blockTemplates =
            templates
                .filterValues { it.block != null }
                .mapValues { (_, item) -> item.block as BlockTemplate }
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

    fun parse(file: File): ItemTemplate? {
        plugin.logger.log(Level.INFO, "Parsing '${file.name}'")

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
            plugin.logger.log(Level.SEVERE, e.message)
            return null
        }

        this.triggers[key] = template.triggers

        return template
    }
}