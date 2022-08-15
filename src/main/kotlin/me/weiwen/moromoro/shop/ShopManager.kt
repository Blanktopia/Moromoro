package me.weiwen.moromoro.shop

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.weiwen.moromoro.Manager
import me.weiwen.moromoro.Moromoro.Companion.plugin
import me.weiwen.moromoro.actions.actionModule
import org.bukkit.entity.Player
import java.io.File
import java.util.logging.Level

object ShopManager : Manager {
    var shopTemplates: MutableMap<String, ShopTemplate> = mutableMapOf()
        private set

    override fun enable() {
        load()
    }

    fun show(player: Player, shop: String) {
        val shopTemplate = shopTemplates[shop] ?: return
        shopTemplate.show(player)
    }

    fun load() {
        val directory = File(plugin.dataFolder, "shops")

        if (!directory.isDirectory) {
            directory.mkdirs()
        }

        shopTemplates.clear()

        // We process files closer to the root first, so we can resolve dependency issues with nesting
        val files = directory
            .walkBottomUp()
            .onEnter { !it.name.startsWith("_") }
            .filter { file -> file.extension in setOf("json", "yml", "yaml") }
            .sortedBy { it.toPath().nameCount }
        for (file in files) {
            val template = parse(file) ?: continue
            shopTemplates[file.nameWithoutExtension] = template
        }
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
    private fun parse(file: File): ShopTemplate? {
        val key = file.nameWithoutExtension

        val format = when (file.extension) {
            "json" -> json
            "yml", "yaml" -> yaml
            else -> return null
        }

        val text = file.readText()
        val template = try {
            format.decodeFromString<ShopTemplate>(text)
        } catch (e: Exception) {
            plugin.logger.log(Level.SEVERE, "Error parsing '${file.name}': ${e.message}")
            return null
        }

        return template
    }
}