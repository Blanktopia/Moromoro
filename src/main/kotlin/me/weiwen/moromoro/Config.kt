package me.weiwen.moromoro

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.logging.Level

@Serializable
data class MoromoroConfig(
    val namespace: String = "moromoro",
)

fun parseConfig(plugin: JavaPlugin): MoromoroConfig {
    val file = File(plugin.dataFolder, "config.yml")

    if (!file.exists()) {
        plugin.logger.log(Level.INFO, "Config file not found, creating default")
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