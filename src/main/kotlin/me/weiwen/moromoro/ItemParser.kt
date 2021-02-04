package me.weiwen.moromoro

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.weiwen.moromoro.actions.actionModule
import java.io.File
import java.util.logging.Level

class ItemParser(private val plugin: Moromoro) {
    private val format = Json {
        serializersModule = actionModule
    }

    fun parse(file: File): ItemTemplate? {
        plugin.logger.log(Level.INFO, "Parsing ${file.name}")

        val key = file.nameWithoutExtension

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
