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
    val model: String,
    val type: String = "model",
)

@Serializable
data class RangeDispatch(
    val entries: List<RangeDispatchEntry>,
    val fallback: Model,
    val type: String = "range_dispatch",
    val property: String = "custom_model_data",
)

@Serializable
data class RangeDispatchEntry(
    val threshold: Int,
    val model: Model,
)

@Serializable
data class Select(
    val cases: List<SelectCase>,
    val fallback: Model,
    val type: String = "select",
    val property: String = "custom_model_data",
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
    val rangeDispatches: MutableMap<Material, MutableList<RangeDispatchEntry>> = mutableMapOf()

    for ((key, item) in templates) {
        for (model in item.models) {
            rangeDispatches.getOrPut(model.material) { mutableListOf() }.add(RangeDispatchEntry(model.predicate.custom_model_data, Model(model.model)))
        }

        val model = item.model ?: continue
//        selects.getOrPut(item.material) { mutableListOf() }.add(SelectCase(key, Model(model)))
        val customModelData = item.customModelData ?: continue
        rangeDispatches.getOrPut(item.material) { mutableListOf() }.add(RangeDispatchEntry(customModelData, Model(model)))
    }

    for ((material, entries) in rangeDispatches) {
        val path = "items/${material.name.lowercase()}.json"
        val json = defaultItem(entries, "item/${material.key.key}")
        val file = File(root, path)
        file.parentFile.mkdirs()
        file.writeText(JsonObject(json).toString())
    }
}

private val json = Json { encodeDefaults = true }

fun defaultItem(entries: List<RangeDispatchEntry>, fallbackModel: String): JsonObject {
    return JsonObject(
        mapOf(
            Pair(
                "model",
                // json.encodeToJsonElement(Select(cases, Model(fallbackModel)))
                json.encodeToJsonElement(RangeDispatch(entries, Model(fallbackModel)))
            )
        )
    )
}
