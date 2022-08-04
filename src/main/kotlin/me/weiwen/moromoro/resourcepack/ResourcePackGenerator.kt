package me.weiwen.moromoro.resourcepack

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.items.ItemManager
import org.bukkit.Material
import java.io.File
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Serializable
data class Model(
    val material: Material,
    @SerialName("custom-model-data")
    val customModelData: Int,
    val model: String,
)

@Serializable
data class ModelOverride(
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
)

class ResourcePackGenerator(private val plugin: Moromoro, private val itemManager: ItemManager) {
    fun generate() {
        val root = File(plugin.dataFolder, "pack/assets/minecraft/models")

        val overrides: MutableMap<String, MutableList<ModelOverride>> = mutableMapOf()

        for (item in itemManager.templates.values) {
            item.models.forEach {
                val modelOverrides = overrides.getOrPut(modelPath(it.material)) { mutableListOf() }
                modelOverrides.add(
                    ModelOverride(
                        model = it.model,
                        predicate = Predicate(custom_model_data = it.customModelData),
                    )
                )
            }

            val model = item.model ?: continue
            val customModelData = item.customModelData ?: continue

            when (item.material) {
                Material.BOW -> {
                    val modelOverrides = overrides.getOrPut(modelPath(item.material)) { mutableListOf() }
                    modelOverrides.addAll(
                        sequenceOf(
                            ModelOverride(
                                model = model,
                                predicate = Predicate(custom_model_data = customModelData),
                            ),
                            ModelOverride(
                                model = "${model}_pulling_0",
                                predicate = Predicate(custom_model_data = customModelData, pulling = 1),
                            ),
                            ModelOverride(
                                model = "${model}_pulling_1",
                                predicate = Predicate(custom_model_data = customModelData, pulling = 1, pull = 0.65),
                            ),
                            ModelOverride(
                                model = "${model}_pulling_2",
                                predicate = Predicate(custom_model_data = customModelData, pulling = 1, pull = 0.9),
                            ),
                        )
                    )
                }
                Material.CROSSBOW -> {
                    val modelOverrides = overrides.getOrPut(modelPath(item.material)) { mutableListOf() }
                    modelOverrides.addAll(
                        sequenceOf(
                            ModelOverride(
                                model = model,
                                predicate = Predicate(custom_model_data = customModelData),
                            ),
                            ModelOverride(
                                model = "${model}_pulling_0",
                                predicate = Predicate(custom_model_data = customModelData, pulling = 1),
                            ),
                            ModelOverride(
                                model = "${model}_pulling_1",
                                predicate = Predicate(custom_model_data = customModelData, pulling = 1, pull = 0.65),
                            ),
                            ModelOverride(
                                model = "${model}_pulling_2",
                                predicate = Predicate(custom_model_data = customModelData, pulling = 1, pull = 0.9),
                            ),
                            ModelOverride(
                                model = "${model}_pulling_2",
                                predicate = Predicate(custom_model_data = customModelData, pulling = 1, pull = 0.9),
                            ),
                            ModelOverride(
                                model = "${model}_arrow",
                                predicate = Predicate(custom_model_data = customModelData, charged = 1),
                            ),
                            ModelOverride(
                                model = "${model}_firework",
                                predicate = Predicate(custom_model_data = customModelData, charged = 1, firework = 1),
                            ),
                        )
                    )
                }
                else -> {
                    val override = ModelOverride(
                        model = model,
                        predicate = Predicate(custom_model_data = customModelData)
                    )
                    overrides.getOrPut(modelPath(item.material)) { mutableListOf() }.add(override)
                }
            }
        }

        for ((path, overrides) in overrides.entries) {
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

        bundleResourcePack()
    }

    private fun modelPath(material: Material): String {
        return if (material.isBlock) {
            "block/${material.name.lowercase()}.json"
        } else {
            "item/${material.name.lowercase()}.json"
        }
    }

    private fun defaultModel(path: String): JsonObject {
        val inputStream = plugin.getResource("default/models/$path")
        if (inputStream == null) {
            plugin.logger.severe("Cannot find resource: default/models/$path")
            return JsonObject(mapOf())
        }

        val streamReader = InputStreamReader(inputStream)
        return Json.parseToJsonElement(streamReader.readText()).jsonObject
    }

    private fun bundleResourcePack() {
        val zip = File(plugin.dataFolder, "pack.zip")
        val fos = zip.outputStream()
        val zos = ZipOutputStream(fos)
        zos.setLevel(9)

        val folder = File(plugin.dataFolder, "pack").path
        val pp = Paths.get(folder)
        val paths = Files.walk(pp)
        paths.filter { !Files.isDirectory(it) }.forEach {
            zos.putNextEntry(ZipEntry(pp.relativize(it).toString()))
            Files.copy(it, zos)
            zos.closeEntry()
        }

        zos.close()
        fos.close()
    }
}