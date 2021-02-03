package me.weiwen.monogoto

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
import com.uchuhimo.konf.source.Source
import com.uchuhimo.konf.source.asSource
import com.uchuhimo.konf.source.toml
import com.uchuhimo.konf.source.yaml
import org.bukkit.Material
import java.io.File
import java.util.logging.Level

object Spec : ConfigSpec("") {
    val material by required<Material>()
    val name by optional<String?>(null)
}

class ItemParser(private val plugin: Monogoto) {
    fun parse(file: File): ItemTemplate? {
        plugin.logger.log(Level.INFO, "Parsing ${file.name}")

        val source = when (file.extension) {
            "toml" -> Source.from.toml.file(file, false)
            "json" -> Source.from.json.file(file, false)
            "yaml" -> Source.from.yaml.file(file, false)
            else -> return null
        }

        val config = Config { addSpec(Spec) }.withSource(source)

        plugin.logger.log(Level.INFO, "$config")

        config.validateRequired()

        return ItemTemplate(
            config[Spec.material],
            config[Spec.name]
        )
    }
}
