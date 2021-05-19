package me.weiwen.moromoro.managers

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.weiwen.moromoro.ItemProperties
import me.weiwen.moromoro.ItemTemplate
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.actionModule
import java.io.File
import java.util.logging.Level

class ItemParser(private val plugin: Moromoro) {
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
