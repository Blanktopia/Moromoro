import kotlinx.serialization.json.*
import me.weiwen.moromoro.Moromoro.Companion.plugin
import me.weiwen.moromoro.items.ItemTemplate
import net.kyori.adventure.key.Key
import java.io.File

// Item definitions stay raw JSON, so that fields of new client versions (for example `transformation`) pass through.

private val CUSTOM_MODEL_DATA = Key.key("custom_model_data")
private val RANGE_DISPATCH = Key.key("range_dispatch")
private val SELECT = Key.key("select")

fun generateItemModels(templates: Map<String, ItemTemplate>) {
    val root = File(plugin.dataFolder, "pack/assets/")

    for ((_, item) in templates) {
        if (item.itemModel == null) continue
        val key = item.itemModel
        val path = "${key.namespace()}/items/${key.value()}.json"
        if (root.resolve(path).exists()) continue
        val json = buildJsonObject { put("model", basicModel(key)) }
        val file = File(root, path)
        file.parentFile.mkdirs()
        file.writeText(json.toString())
    }
}

fun generateItems(templates: Map<String, ItemTemplate>) {
    val root = File(plugin.dataFolder, "pack/assets/minecraft")

    val itemModels: MutableMap<Key, MutableMap<Double, JsonElement>> = mutableMapOf()

    for ((_, item) in templates) {
        val model = item.customModel?.let { Json.parseToJsonElement(it) } ?: item.model?.let { basicModel(it) } ?: continue
        val customModelData = item.customModelData ?: continue
        itemModels.getOrPut(item.item) { mutableMapOf() }[customModelData] = model
    }

    val overlays = File(plugin.dataFolder, "pack-overlays").listFiles { file, s -> file.isDirectory && !s.startsWith("_") }
    for (overlay in overlays) {
        val itemsDir = overlay.resolve("assets/minecraft/items")
        val items = itemsDir.listFiles()?.map { file -> file.nameWithoutExtension } ?: continue
        for (item in items) {
            itemModels.putIfAbsent(Key.key(item), mutableMapOf())
        }
    }

    for ((item, customModels) in itemModels) {
        val path = "items/${item.value()}.json"
        val default = defaultItem(item)
        var fallback = default.getValue("model")
        val selectCases = mutableListOf<JsonElement>()
        for (overlay in overlays) {
            val overlayItemFile = overlay.resolve("assets/minecraft/${path}")
            if (!overlayItemFile.isFile) continue
            val overlayModel = Json.parseToJsonElement(overlayItemFile.readText()).jsonObject.getValue("model").jsonObject
            val onCustomModelData = overlayModel.key("property") == CUSTOM_MODEL_DATA
            val type = overlayModel.key("type")
            when {
                onCustomModelData && type == RANGE_DISPATCH -> for (entry in overlayModel.getValue("entries").jsonArray) {
                    customModels[entry.jsonObject.getValue("threshold").jsonPrimitive.double] = entry.jsonObject.getValue("model")
                }
                onCustomModelData && type == SELECT -> selectCases.addAll(overlayModel.getValue("cases").jsonArray)
                // An overlay model that does not dispatch on custom_model_data replaces the vanilla model.
                else -> fallback = overlayModel
            }
        }
        if (selectCases.isNotEmpty()) fallback = selectModel(selectCases, fallback)
        val json = JsonObject(default + ("model" to mergeModels(customModels, fallback)))
        val file = File(root, path)
        file.parentFile.mkdirs()
        file.writeText(json.toString())
    }
}

fun defaultItem(item: Key): JsonObject {
    if (item.namespace() == "minecraft") {
        val file = plugin.getResource("default/items/${item.value()}.json")
        if (file != null) {
            return Json.parseToJsonElement(file.reader().use { it.readText() }).jsonObject
        } else {
            plugin.logger.info("Failed to load default model: ${item.value()}")
        }
    }
    return buildJsonObject { put("model", basicModel(Key.key(item.namespace(), "item/${item.value()}"))) }
}

fun mergeModels(customModels: Map<Double, JsonElement>, fallback: JsonElement): JsonObject = buildJsonObject {
    put("type", "range_dispatch")
    put("property", "custom_model_data")
    putJsonArray("entries") {
        for ((threshold, model) in customModels.entries.sortedBy { it.key }) {
            addJsonObject {
                put("threshold", threshold)
                put("model", model)
            }
        }
    }
    put("fallback", fallback)
}

// The strings of custom_model_data (for example the Dungeons and Taverns charts) select a model when no float entry matches.
private fun selectModel(cases: List<JsonElement>, fallback: JsonElement): JsonObject = buildJsonObject {
    put("type", "select")
    put("property", "custom_model_data")
    put("cases", JsonArray(cases))
    put("fallback", fallback)
}

private fun basicModel(key: Key): JsonObject = buildJsonObject {
    put("type", "model")
    put("model", key.asMinimalString())
}

private fun JsonObject.key(name: String): Key? = this[name]?.jsonPrimitive?.content?.let { Key.key(it) }
