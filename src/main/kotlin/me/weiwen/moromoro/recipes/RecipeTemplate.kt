@file:UseSerializers(ItemStackSerializer::class, RecipeChoiceSerializer::class)

package me.weiwen.moromoro.recipes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.serializers.ItemStackSerializer
import me.weiwen.moromoro.serializers.RecipeChoiceSerializer
import org.bukkit.NamespacedKey
import org.bukkit.inventory.*

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
