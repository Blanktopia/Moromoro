package me.weiwen.moromoro.resourcepack

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import me.weiwen.moromoro.Moromoro.Companion.plugin
import me.weiwen.moromoro.blocks.BlockTemplate
import me.weiwen.moromoro.blocks.MushroomBlockTemplate
import org.bukkit.Material
import java.io.File
import java.io.InputStreamReader

@Serializable
data class Multipart(
    @SerialName("when")
    val state: MultipartState,
    @SerialName("apply")
    val model: BlockState,
)

@Serializable
data class MultipartState(
    val down: Boolean,
    val east: Boolean,
    val north: Boolean,
    val south: Boolean,
    val up: Boolean,
    val west: Boolean,
)

@Serializable
data class BlockState(
    val model: String,
)

fun MultipartState.Companion.from(state: Int): MultipartState =
    MultipartState(
        down = /*   */ state.and(1 shl 0) != 0,
        east = /*   */ state.and(1 shl 1) != 0,
        north = /*  */ state.and(1 shl 2) != 0,
        south = /*  */ state.and(1 shl 3) != 0,
        up = /*     */ state.and(1 shl 4) != 0,
        west = /*   */ state.and(1 shl 5) != 0,
    )

fun blockStatePath(material: Material): String = "${material.name.lowercase()}.json"

fun generateMushroomBlocks(templates: Iterable<BlockTemplate>) {
    val root = File(plugin.dataFolder, "pack/assets/minecraft/blockstates")

    val multiparts: Map<Material, MutableList<Multipart>> = mapOf(
        Material.BROWN_MUSHROOM_BLOCK to mutableListOf(),
        Material.RED_MUSHROOM_BLOCK to mutableListOf(),
        Material.MUSHROOM_STEM to mutableListOf(),
    )

    for (block in templates.filterIsInstance<MushroomBlockTemplate>()) {
        val multipart = multiparts[block.material]
        if (multipart == null) {
            plugin.logger.warning("Invalid material for mushroom block, should be one of BROWN_MUSHROOM_BLOCK or RED_MUSHROOM_BLOCK")
            continue
        }
        val state = MultipartState.from(block.state)
        multipart.add(Multipart(state = state, model = BlockState(block.model)))
    }

    for ((material, multipart) in multiparts.entries) {
        val path = blockStatePath(material)
        val json = defaultBlockState(path).toMutableMap()
        val jsonMultipart = json["multipart"]?.jsonArray?.toMutableList() ?: mutableListOf()
        jsonMultipart.addAll(multipart.map { Json.encodeToJsonElement(it) })

        json["multipart"] = JsonArray(jsonMultipart)

        val file = File(root, path)
        file.parentFile.mkdirs()
        file.writeText(JsonObject(json).toString())
    }
}

fun defaultBlockState(path: String): JsonObject {
    val inputStream = plugin.getResource("default/blockstates/$path")
    if (inputStream == null) {
        plugin.logger.severe("Cannot find resource: default/blockstates/$path")
        return JsonObject(mapOf())
    }

    val streamReader = InputStreamReader(inputStream)
    return Json.parseToJsonElement(streamReader.readText()).jsonObject
}
