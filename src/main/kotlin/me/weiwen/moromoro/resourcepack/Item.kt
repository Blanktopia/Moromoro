package me.weiwen.moromoro.resourcepack

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import me.weiwen.moromoro.Moromoro.Companion.plugin
import me.weiwen.moromoro.items.ItemTemplate
import org.bukkit.Material
import java.io.File

@Serializable
data class ItemModel(
    val material: Material,
    val predicate: Predicate,
    val model: String,
)

@Serializable
data class Model(
    val type: String,
    val model: String,
)

@Serializable
data class Select(
    val type: String,
    val property: String,
    val cases: List<SelectCase>,
    val fallback: Model,
)

@Serializable
data class SelectCase(
    @SerialName("when") val case: String,
    val model: Model,
)

@Serializable
data class ItemModelOverride(
    val model: String,
    val predicate: Predicate,
)

@Serializable
data class Predicate(
    val custom_model_data: Int,
    val pulling: Int? = null,
    val pull: Double? = null,
    val charged: Int? = null,
    val firework: Int? = null,
    val blocking: Int? = null,
)

fun generateItems(templates: Map<String, ItemTemplate>) {
    val root = File(plugin.dataFolder, "pack/assets/minecraft")

    val selects: MutableMap<Material, MutableList<SelectCase>> = mutableMapOf()

    for ((key, item) in templates) {
//        for (model in item.models) {
//            overrides
//                .getOrPut(model.material) { mutableListOf() }
//                .add(ItemModelOverride(model = model.model, predicate = model.predicate))
//        }

        val model = item.model ?: continue
        selects.getOrPut(item.material) { mutableListOf() }.add(SelectCase(key, Model("model", model)))
    }

    for ((material, cases) in selects) {
        val path = "items/${material.name.lowercase()}.json"
        val json = defaultItem(cases, "item/${material.key.key}")
        val file = File(root, path)
        file.parentFile.mkdirs()
        file.writeText(JsonObject(json).toString())
    }
}

fun defaultItem(cases: List<SelectCase>, fallbackModel: String): JsonObject {
    return JsonObject(
        mapOf(
            Pair(
                "model", Json.encodeToJsonElement(
                    Select(
                        "select", "custom_model_data", cases, Model("model", fallbackModel)
                    )
                )
            )
        )
    )
}
