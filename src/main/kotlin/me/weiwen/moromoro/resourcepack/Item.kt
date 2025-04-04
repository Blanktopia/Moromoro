@file:UseSerializers(
    KeySerializer::class,
)
@file:OptIn(ExperimentalSerializationApi::class)

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.*
import me.weiwen.moromoro.Moromoro.Companion.plugin
import me.weiwen.moromoro.items.ItemTemplate
import me.weiwen.moromoro.serializers.KeySerializer
import net.kyori.adventure.key.Key
import java.io.File

@Serializable
data class ItemModelFile(
    val model: ItemModel
)

@Serializable
data class Model(
    val parent: String,
    val textures: Map<String, String>
)

@Serializable
sealed interface ItemModel {}

@Serializable
@SerialName("minecraft:condition")
data class MinecraftConditionItemModel(
    val property: Key,
    val on_false: ItemModel,
    val on_true: ItemModel,
) : ItemModel

@Serializable
@SerialName("condition")
data class ConditionItemModel(
    val property: Key,
    val on_false: ItemModel,
    val on_true: ItemModel,
) : ItemModel

@Serializable
@SerialName("minecraft:model")
data class MinecraftBasicItemModel(
    val model: Key,
    val tints: JsonArray? = null,
) : ItemModel

@Serializable
@SerialName("model")
data class BasicItemModel(
    val model: Key,
    val tints: JsonArray? = null,
) : ItemModel

@Serializable
@SerialName("minecraft:range_dispatch")
data class MinecraftRangeDispatchItemModel(
    val property: Key,
    val entries: MutableList<RangeDispatchItemModelEntry>,
    val scale: Double? = null,
    var fallback: ItemModel? = null,
) : ItemModel

@Serializable
@SerialName("range_dispatch")
data class RangeDispatchItemModel(
    val property: Key,
    val entries: MutableList<RangeDispatchItemModelEntry>,
    val scale: Double? = null,
    var fallback: ItemModel? = null,
) : ItemModel

@Serializable
data class RangeDispatchItemModelEntry(
    val threshold: Double,
    val model: ItemModel,
)

@Serializable
@SerialName("minecraft:select")
data class MinecraftSelectItemModel(
    val property: Key,
    val cases: MutableList<SelectItemModelSwitchCase>,
    var fallback: ItemModel? = null,
) : ItemModel

@Serializable
@SerialName("select")
data class SelectItemModel(
    val property: Key,
    val cases: MutableList<SelectItemModelSwitchCase>,
    var fallback: ItemModel? = null,
) : ItemModel

@Serializable
data class SelectItemModelSwitchCase(
    @Serializable(with = ListOrStringSerializer::class)
    val `when`: List<String>,
    val model: ItemModel,
)

data class ListOrString(val list: List<String>)
object ListOrStringSerializer :
    JsonTransformingSerializer<List<String>>(ListSerializer(String.serializer())) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        if (element is JsonArray) return element
        return JsonArray(listOf(element.jsonPrimitive))
    }

    override fun transformSerialize(element: JsonElement): JsonElement {
        if (element !is JsonArray) return element
        if (element.size != 1) return element
        return element[0]
    }
}

@Serializable
@SerialName("minecraft:empty")
data object MinecraftEmptyItemModel : ItemModel

@Serializable
@SerialName("empty")
data object EmptyItemModel : ItemModel

@Serializable
@SerialName("minecraft:bundle/selected_item")
data object MinecraftBundleSelectedItemItemModel : ItemModel

@Serializable
@SerialName("bundle/selected_item")
data object BundleSelectedItemItemModel : ItemModel

@Serializable
@SerialName("minecraft:special")
data class MinecraftSpecialItemModel(
    val model: SpecialItemModelType,
    val base: Key,
) : ItemModel

@Serializable
@SerialName("special")
data class SpecialItemModel(
    val model: SpecialItemModelType,
    val base: Key,
) : ItemModel

@Serializable
data class SpecialItemModelType(
    val type: Key,
)

fun generateItemModels(templates: Map<String, ItemTemplate>) {
    val root = File(plugin.dataFolder, "pack/assets/")

    for ((_, item) in templates) {
        if (item.itemModel == null) continue
        val key = item.itemModel
        val path = "${key.namespace()}/items/${key.value()}.json"
        if (root.resolve(path).exists()) continue
        val model = ItemModelFile(BasicItemModel(key))
        val json = Json.encodeToJsonElement(model)
        val file = File(root, path)
        file.parentFile.mkdirs()
        file.writeText(json.toString())
    }
}

fun generateItems(templates: Map<String, ItemTemplate>) {
    val root = File(plugin.dataFolder, "pack/assets/minecraft")

    val itemModels: MutableMap<Key, MutableMap<Double, ItemModel>> = mutableMapOf()

    for ((_, item) in templates) {
        val model = item.customModel?.let { Json.decodeFromString<ItemModel>(it) } ?: item.model?.let { BasicItemModel(it) } ?: continue
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
        for (overlay in overlays) {
            val overlayItemFile = overlay.resolve("assets/minecraft/${path}")
            if (overlayItemFile.exists() && overlayItemFile.isFile) {
                val overlayItem = Json.decodeFromStream<ItemModelFile>(overlayItemFile.inputStream())
                if (overlayItem.model is RangeDispatchItemModel && overlayItem.model.property == Key.key("custom_model_data")) {
                    customModels.putAll(overlayItem.model.entries.map { it.threshold to it.model })
                }
            }
        }
        val model = ItemModelFile(mergeModels(customModels, defaultModel(item)))
        val json = Json.encodeToJsonElement(model)
        val file = File(root, path)
        file.parentFile.mkdirs()
        file.writeText(json.toString())
    }
}

fun defaultModel(item: Key): ItemModel {
    if (item.namespace() == "minecraft") {
        val file = plugin.getResource("default/items/${item.value()}.json")
        if (file != null) {
            return Json.decodeFromStream<ItemModelFile>(file).model
        } else {
            plugin.logger.info("Failed to load default model: ${item.value()}")
        }
    }
    return BasicItemModel(Key.key(item.namespace(), "item/${item.value()}"))
}

fun mergeModels(customModels: Map<Double, ItemModel>, fallback: ItemModel): ItemModel {
    val entries = customModels.entries
        .sortedBy { it.key }
        .map { RangeDispatchItemModelEntry(it.key, it.value) }
        .toMutableList()

    return RangeDispatchItemModel(
        Key.key("custom_model_data"),
        entries,
        null,
        fallback,
    )
}