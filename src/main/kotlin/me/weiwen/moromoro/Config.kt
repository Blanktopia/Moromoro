package me.weiwen.moromoro

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.logging.Level

@Serializable
data class MoromoroConfig(
    val namespace: String = "moromoro",
)

fun parseConfig(plugin: JavaPlugin): MoromoroConfig {
    val file = File(plugin.dataFolder, "config.yml")
    return try {
        Yaml(
            configuration = YamlConfiguration(
                polymorphismStyle = PolymorphismStyle.Property
            )
        ).decodeFromString<MoromoroConfig>(file.readText())
    } catch (e: Exception) {
        plugin.logger.log(Level.SEVERE, e.message)
        MoromoroConfig()
    }
}