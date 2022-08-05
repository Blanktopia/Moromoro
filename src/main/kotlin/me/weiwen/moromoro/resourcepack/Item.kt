package me.weiwen.moromoro.resourcepack

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import me.weiwen.moromoro.Moromoro.Companion.plugin
import me.weiwen.moromoro.items.ItemTemplate
import org.bukkit.Material
import java.io.File
import java.io.InputStreamReader

@Serializable
data class ItemModel(
    val material: Material,
    val predicate: Predicate,
    val model: String,
)

@Serializable
data class ItemModelOverride(
    val model: String,
    val predicate: Predicate
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

fun itemModelPath(material: Material): String = "item/${material.name.lowercase()}.json"

fun generateItems(templates: Iterable<ItemTemplate>) {
    val root = File(plugin.dataFolder, "pack/assets/minecraft/models")

    val overrides: MutableMap<Material, MutableList<ItemModelOverride>> = mutableMapOf()

    for (item in templates) {
        for (model in item.models) {
            overrides
                .getOrPut(model.material) { mutableListOf() }
                .add(ItemModelOverride(model = model.model, predicate = model.predicate))
        }

        val model = item.model ?: continue
        val customModelData = item.customModelData ?: continue
        overrides
            .getOrPut(item.material) { mutableListOf() }
            .add(ItemModelOverride(model = model, predicate = Predicate(custom_model_data = customModelData)))
    }

    for ((material, overrides) in overrides.entries) {
        val path = itemModelPath(material)
        val json = defaultModel(path).toMutableMap()
        val jsonOverrides = json["overrides"]?.jsonArray?.toMutableList() ?: mutableListOf()
        jsonOverrides.addAll(
            overrides
                .sortedBy { it.predicate.custom_model_data }
                .map { Json.encodeToJsonElement(it) }
        )

        json["overrides"] = JsonArray(jsonOverrides)

        val file = File(root, path)
        file.parentFile.mkdirs()
        file.writeText(JsonObject(json).toString())
    }
}

fun defaultModel(path: String): JsonObject {
    val inputStream = plugin.getResource("default/models/$path")
    if (inputStream == null) {
        plugin.logger.severe("Cannot find resource: default/models/$path")
        return JsonObject(mapOf())
    }

    val streamReader = InputStreamReader(inputStream)
    return Json.parseToJsonElement(streamReader.readText()).jsonObject
}
