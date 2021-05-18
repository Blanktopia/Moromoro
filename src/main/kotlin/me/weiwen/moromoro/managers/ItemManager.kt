package me.weiwen.moromoro.managers

import me.weiwen.moromoro.ItemTemplate
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Trigger
import java.io.File
import java.util.logging.Level

class ItemManager(val plugin: Moromoro) {
    var keys: Set<String> = setOf()
        private set
    var templates: Map<String, ItemTemplate> = mapOf()
        private set
    var triggers: MutableMap<String, Map<Trigger, List<Action>>> = mutableMapOf()
        private set

    fun load() {
        val directory = File(plugin.dataFolder, "items")

        if (directory.isDirectory) {
            directory.mkdirs()
        }

        // We walk bottom up so that the files closer to the root are processed last, and will take priority.
        val files = directory.walkBottomUp().filter { file -> file.extension in listOf("toml", "json", "yaml") }

        triggers.clear()

        templates = files
            .mapNotNull { file ->
                plugin.logger.log(Level.INFO, "Parsing '${file.name}'")
                plugin.itemParser.parse(file)?.let { Pair(file.nameWithoutExtension, it) }
            }
            .associate { it }

        keys = templates.keys
    }

    fun registerTriggers(key: String, triggers: Map<Trigger, List<Action>>) {
        this.triggers[key] = triggers
    }
}
