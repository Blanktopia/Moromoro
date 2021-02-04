package me.weiwen.moromoro

import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Trigger
import java.io.File

class ItemManager(val plugin: Moromoro) {
    var keys: Set<String> = setOf()
        private set
    var templates: Map<String, ItemTemplate> = mapOf()
        private set
    var triggers: MutableMap<String, Map<Trigger, List<Action>>> = mutableMapOf()
        private set

    fun load() {
        val directory = File(plugin.dataFolder, "items")
        val files = directory.listFiles { file -> file.extension in listOf("toml", "json", "yaml") }

        if (files == null) {
            directory.mkdirs()
            return
        }

        triggers.clear()

        templates = files
            .mapNotNull { file ->
                plugin.itemParser.parse(file)?.let { Pair(file.nameWithoutExtension, it) }
            }
            .associate { it }

        keys = templates.keys
    }

    fun registerTriggers(key: String, triggers: Map<Trigger, List<Action>>) {
        this.triggers[key] = triggers
    }
}
