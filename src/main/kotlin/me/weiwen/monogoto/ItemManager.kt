package me.weiwen.monogoto

import java.io.File
import java.util.logging.Level

class ItemManager(val plugin: Monogoto) {
    var items: Map<String, ItemTemplate> = HashMap()
        private set
    var names: Set<String> = setOf()
        private set

    fun load() {
        val directory = File(plugin.dataFolder, "items")
        val files = directory.listFiles { file -> file.extension in listOf("toml", "json", "yaml") }

        if (files == null) {
            directory.mkdirs()
            return
        }

        items = files
            .mapNotNull { file ->
                plugin.itemParser.parse(file)?.let { Pair(file.nameWithoutExtension, it) }
            }
            .associate { it }

        names = items.keys
    }
}
