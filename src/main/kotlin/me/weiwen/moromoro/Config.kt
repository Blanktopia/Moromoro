package me.weiwen.moromoro

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.logging.Level

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
    val file = File(plugin.dataFolder, "config.yml")

    if (!file.exists()) {
        plugin.logger.log(Level.INFO, "Config file not found, creating default")
        plugin.dataFolder.mkdirs()
        file.createNewFile()
        file.writeText(Yaml().encodeToString(MoromoroConfig()))
    }

    return try {
        Yaml().decodeFromString<MoromoroConfig>(file.readText())
    } catch (e: Exception) {
        plugin.logger.log(Level.SEVERE, e.message)
        MoromoroConfig()
    }
}