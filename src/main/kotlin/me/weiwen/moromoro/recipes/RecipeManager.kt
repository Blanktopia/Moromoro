@file:UseSerializers(ItemStackSerializer::class, RecipeChoiceSerializer::class)

package me.weiwen.moromoro.managers

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.actions.actionModule
import me.weiwen.moromoro.items.ItemManager
import me.weiwen.moromoro.recipes.RecipeListener
import me.weiwen.moromoro.serializers.ItemStackSerializer
import me.weiwen.moromoro.serializers.RecipeChoiceSerializer
import org.bukkit.NamespacedKey
import org.bukkit.inventory.*
import java.io.File
import java.util.logging.Level

@Serializable
sealed class RecipeTemplate {
    abstract fun recipe(key: String): Recipe
}

@Serializable
@SerialName("shaped")
class ShapedRecipeTemplate(
    private val result: ItemStack,
    private val shape: List<String>,
    private val ingredients: Map<Char, RecipeChoice>
) : RecipeTemplate() {
    override fun recipe(key: String): Recipe {
        val recipe = ShapedRecipe(NamespacedKey(Moromoro.plugin.config.namespace, key), result)

        recipe.shape(*shape.toTypedArray())

        ingredients.forEach { (char, ingredient) ->
            recipe.setIngredient(char, ingredient)
        }

        return recipe
    }
}

@Serializable
@SerialName("shapeless")
class ShapelessRecipeTemplate(private val result: ItemStack, private val ingredients: List<RecipeChoice>) :
    RecipeTemplate() {
    override fun recipe(key: String): Recipe {
        val recipe = ShapelessRecipe(NamespacedKey(Moromoro.plugin.config.namespace, key), result)

        ingredients.forEach { ingredient ->
            recipe.addIngredient(ingredient)
        }

        return recipe
    }
}

@Serializable
@SerialName("furnace")
class FurnaceRecipeTemplate(
    private val result: ItemStack,
    private val input: ItemStack,
    private val experience: Float = 0f,
    @SerialName("cooking-time")
    private val cookingTime: Int = 200
) : RecipeTemplate() {
    override fun recipe(key: String): Recipe =
        FurnaceRecipe(
            NamespacedKey(Moromoro.plugin.config.namespace, key),
            result,
            RecipeChoice.ExactChoice(input),
            experience,
            cookingTime
        )
}

@Serializable
@SerialName("campfire")
class CampfireRecipeTemplate(
    private val result: ItemStack,
    private val input: ItemStack,
    private val experience: Float = 0f,
    @SerialName("cooking-time")
    private val cookingTime: Int = 600
) : RecipeTemplate() {
    override fun recipe(key: String): Recipe = CampfireRecipe(
        NamespacedKey(Moromoro.plugin.config.namespace, key),
        result,
        RecipeChoice.ExactChoice(input),
        experience,
        cookingTime
    )
}

@Serializable
@SerialName("smoking")
class SmokingRecipeTemplate(
    private val result: ItemStack,
    private val input: ItemStack,
    private val experience: Float = 0f,
    @SerialName("cooking-time")
    private val cookingTime: Int = 100
) : RecipeTemplate() {
    override fun recipe(key: String): Recipe = SmokingRecipe(
        NamespacedKey(Moromoro.plugin.config.namespace, key),
        result,
        RecipeChoice.ExactChoice(input),
        experience,
        cookingTime
    )
}

@Serializable
@SerialName("blasting")
class BlastingRecipeTemplate(
    private val result: ItemStack,
    private val input: ItemStack,
    private val experience: Float = 0f,
    @SerialName("cooking-time")
    private val cookingTime: Int = 100
) : RecipeTemplate() {
    override fun recipe(key: String): Recipe = BlastingRecipe(
        NamespacedKey(Moromoro.plugin.config.namespace, key),
        result,
        RecipeChoice.ExactChoice(input),
        experience,
        cookingTime
    )
}

@Serializable
@SerialName("stonecutter")
class StonecutterRecipeTemplate(
    private val result: ItemStack,
    private val input: ItemStack,
) : RecipeTemplate() {
    override fun recipe(key: String): Recipe = StonecuttingRecipe(
        NamespacedKey(Moromoro.plugin.config.namespace, key),
        result,
        RecipeChoice.ExactChoice(input),
    )
}

@Serializable
@SerialName("smithing")
class SmithingRecipeTemplate(
    private val result: ItemStack,
    private val input: List<ItemStack>,
    private val copyNbt: Boolean,
) : RecipeTemplate() {
    override fun recipe(key: String): Recipe = SmithingRecipe(
        NamespacedKey(Moromoro.plugin.config.namespace, key),
        result,
        RecipeChoice.ExactChoice(input[0]),
        RecipeChoice.ExactChoice(input[1]),
        copyNbt
    )
}

class RecipeManager(val plugin: Moromoro, private val itemManager: ItemManager) {
    var recipes: Map<NamespacedKey, Recipe> = mapOf()
        private set

    fun enable() {
        plugin.server.pluginManager.registerEvents(RecipeListener(plugin, this, itemManager), plugin)
        load()
    }

    fun disable() {
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