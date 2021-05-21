package me.weiwen.moromoro.managers

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.weiwen.moromoro.ItemProperties
import me.weiwen.moromoro.ItemTemplate
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Trigger
import me.weiwen.moromoro.actions.actionModule
import java.io.File
import java.util.logging.Level

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

    fun registerTriggers(key: String, triggers: Map<Trigger, List<Action>>) {
        this.triggers[key] = triggers
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
        val properties = try {
            format.decodeFromString<ItemProperties>(text)
        } catch (e: Exception) {
            plugin.logger.log(Level.SEVERE, e.message)
            return null
        }

        val template = ItemTemplate(key, properties)
        template.registerTriggers(plugin.itemManager)

        return template
    }
}
