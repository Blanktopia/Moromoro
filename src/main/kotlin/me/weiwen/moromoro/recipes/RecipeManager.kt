package me.weiwen.moromoro.recipes

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.weiwen.moromoro.Manager
import me.weiwen.moromoro.Moromoro.Companion.plugin
import me.weiwen.moromoro.actions.actionModule
import org.bukkit.NamespacedKey
import org.bukkit.inventory.Recipe
import java.io.File
import java.util.logging.Level

object RecipeManager : Manager {
    var recipes: Map<NamespacedKey, Recipe> = mapOf()
        private set

    override fun enable() {
        load()
    }

    override fun disable() {
        recipes.keys.forEach {
            plugin.server.removeRecipe(it)
        }
    }

    fun load() {
        val directory = File(plugin.dataFolder, "recipes")

        if (!directory.isDirectory) {
            directory.mkdirs()
        }

        // We walk bottom up so that the files closer to the root are processed last, and will take priority.
        val files = directory.walkBottomUp().filter { file -> file.extension in setOf("json", "yml", "yaml") }

        recipes.keys.forEach {
            plugin.server.removeRecipe(it)
        }

        recipes = files
            .mapNotNull { file ->
                parse(file)?.let { NamespacedKey(plugin.config.namespace, file.nameWithoutExtension) to it }
            }
            .associate { it }

        recipes.forEach { (_, recipe) ->
            plugin.server.addRecipe(recipe)
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

    fun parse(file: File): Recipe? {
        val key = file.nameWithoutExtension

        val format = when (file.extension) {
            "json" -> json
            "yml", "yaml" -> yaml
            else -> return null
        }

        val text = file.readText()
        val template = try {
            format.decodeFromString<RecipeTemplate>(text)
        } catch (e: Exception) {
            plugin.logger.log(Level.SEVERE, "Error parsing '${file.name}': ${e.message}")
            return null
        }

        return template.recipe(key)
    }
}