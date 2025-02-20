package me.weiwen.moromoro

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

@Serializable
data class MoromoroConfig(
    val namespace: String = "moromoro",
    @SerialName("discover-all-recipes")
    val discoverAllRecipes: Boolean = true,

    @SerialName("tick-interval")
    val tickInterval: Long = 1,
    @SerialName("tick-slow-interval")
    val tickSlowInterval: Long = 5,
    @SerialName("projectile-tick-interval")
    val projectileTickInterval: Long = 2,

    @SerialName("resource-pack-url")
    val resourcePackUrl: String? = null,
    @SerialName("resource-pack-hash")
    val resourcePackHash: String? = null,

    @SerialName("render-distance")
    val renderDistance: Double = 32.0,
    @SerialName("render-interval")
    val renderInterval: Int = 3,

    // Debug
    @SerialName("force-migration")
    val forceMigration: Boolean = false,
)

fun parseConfig(plugin: JavaPlugin): MoromoroConfig {
    return parseConfig(plugin.componentLogger, plugin.dataFolder)
}

fun parseConfig(logger: ComponentLogger, dataFolder: File): MoromoroConfig {
    val file = File(dataFolder, "config.yml")

    if (!file.exists()) {
        logger.info("Config file not found, creating default")
        dataFolder.mkdirs()
        file.createNewFile()
        file.writeText(Yaml().encodeToString(MoromoroConfig()))
    }

    return try {
        Yaml().decodeFromString<MoromoroConfig>(file.readText())
    } catch (e: Exception) {
        logger.error(e.message)
        MoromoroConfig()
    }
}