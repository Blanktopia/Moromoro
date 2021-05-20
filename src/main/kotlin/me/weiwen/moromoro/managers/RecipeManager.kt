@file:UseSerializers(ItemStackSerializer::class)

package me.weiwen.moromoro.managers

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.actionModule
import me.weiwen.moromoro.serializers.ItemStackSerializer
import org.bukkit.NamespacedKey
import org.bukkit.inventory.*
import java.io.File
import java.util.logging.Level

@Serializable
sealed class RecipeTemplate {
    lateinit var key: String

    abstract val recipe: Recipe
}

@Serializable
@SerialName("shaped")
class ShapedRecipeTemplate(
    val result: ItemStack,
    val shape: List<String>,
    val ingredients: Map<Char, ItemStack>
) : RecipeTemplate() {
    override val recipe: Recipe
        get() = ShapedRecipe(NamespacedKey(Moromoro.plugin.config.namespace, key), result).also {
            it.shape(*shape.toTypedArray())
            ingredients.forEach { (char, ingredient) -> it.setIngredient(char, ingredient) }
        }
}

@Serializable
@SerialName("shapeless")
class ShapelessRecipeTemplate(val result: ItemStack, val ingredients: List<ItemStack>) :
    RecipeTemplate() {
    override val recipe: Recipe
        get() = ShapelessRecipe(NamespacedKey(Moromoro.plugin.config.namespace, key), result).also {
            ingredients.forEach { ingredient -> it.addIngredient(ingredient) }
        }
}

@Serializable
@SerialName("furnace")
class FurnaceRecipeTemplate(
    val result: ItemStack,
    val input: ItemStack,
    val experience: Float = 0f,
    @SerialName("cooking-time")
    val cookingTime: Int = 200
) : RecipeTemplate() {
    override val recipe: Recipe
        get() = FurnaceRecipe(NamespacedKey(Moromoro.plugin.config.namespace, key), result, RecipeChoice.ExactChoice(input), experience, cookingTime)
}

@Serializable
@SerialName("campfire")
class CampfireRecipeTemplate(
    val result: ItemStack,
    val input: ItemStack,
    val experience: Float = 0f,
    @SerialName("cooking-time")
    val cookingTime: Int = 600
) : RecipeTemplate() {
    override val recipe: Recipe
        get() = CampfireRecipe(NamespacedKey(Moromoro.plugin.config.namespace, key), result, RecipeChoice.ExactChoice(input), experience, cookingTime)
}

@Serializable
@SerialName("smoking")
class SmokingRecipeTemplate(
    val result: ItemStack,
    val input: ItemStack,
    val experience: Float = 0f,
    @SerialName("cooking-time")
    val cookingTime: Int = 100
) : RecipeTemplate() {
    override val recipe: Recipe
        get() = SmokingRecipe(NamespacedKey(Moromoro.plugin.config.namespace, key), result, RecipeChoice.ExactChoice(input), experience, cookingTime)
}

@Serializable
@SerialName("blasting")
class BlastingRecipeTemplate(
    val result: ItemStack,
    val input: ItemStack,
    val experience: Float = 0f,
    @SerialName("cooking-time")
    val cookingTime: Int = 100
) : RecipeTemplate() {
    override val recipe: Recipe
        get() = BlastingRecipe(NamespacedKey(Moromoro.plugin.config.namespace, key), result, RecipeChoice.ExactChoice(input), experience, cookingTime)
}

class RecipeManager(val plugin: Moromoro) {
    var recipes: Map<String, Recipe> = mapOf()
        private set

    fun enable() {
        load()
    }

    fun disable() {
        recipes.keys.forEach {
            plugin.server.removeRecipe(NamespacedKey(plugin.config.namespace, it))
        }
    }

    fun load() {
        val directory = File(plugin.dataFolder, "recipes")

        if (directory.isDirectory) {
            directory.mkdirs()
        }

        // We walk bottom up so that the files closer to the root are processed last, and will take priority.
        val files = directory.walkBottomUp().filter { file -> file.extension in setOf("json", "yml", "yaml") }

        recipes.keys.forEach {
            plugin.server.removeRecipe(NamespacedKey(plugin.config.namespace, it))
        }

        recipes = files
            .mapNotNull { file -> parse(file)?.let { Pair(file.nameWithoutExtension, it) } }
            .associate { it }

       recipes.forEach { _, recipe ->
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
        plugin.logger.log(Level.INFO, "Parsing '${file.name}'")

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
            plugin.logger.log(Level.SEVERE, e.message)
            return null
        }

        template.key = key

        return template.recipe
    }
}