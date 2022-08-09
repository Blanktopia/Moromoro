package me.weiwen.moromoro.items

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Trigger
import me.weiwen.moromoro.actions.actionModule
import me.weiwen.moromoro.extensions.customItemKey
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.io.File
import java.util.logging.Level

class ItemManager(val plugin: Moromoro) {
    var keys: Set<String> = setOf()
        private set
    var templates: MutableMap<String, ItemTemplate> = mutableMapOf()
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

        triggers.clear()
        templates.clear()

        // We process files closer to the root first, so we can resolve dependency issues with nesting
        val files = directory
            .walkBottomUp()
            .onEnter { !it.name.startsWith("_") }
            .filter { file -> file.extension in setOf("json", "yml", "yaml") }
            .sortedBy { it.toPath().nameCount }
        for (file in files) {
            val template = parse(file) ?: continue
            templates[file.nameWithoutExtension] = template
        }

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

    @OptIn(ExperimentalSerializationApi::class)
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
            val item = Moromoro.plugin.essentialsHook.getItemStack(alias)
            return item ?: ItemStack(Material.STICK)
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
        pages.populateWithItemStacks(
            templates
                .entries
                .filter { (_, template) -> template.alias == null }
                .map { (key, template) -> template.item(key) }
        )
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