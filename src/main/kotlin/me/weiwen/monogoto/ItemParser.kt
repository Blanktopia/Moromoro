package me.weiwen.monogoto

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
import com.uchuhimo.konf.UnsetValueException
import com.uchuhimo.konf.source.LoadException
import com.uchuhimo.konf.source.Source
import com.uchuhimo.konf.source.toml
import com.uchuhimo.konf.source.yaml
import com.uchuhimo.konf.toValue
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentWrapper
import java.io.File
import java.util.logging.Level

object Spec : ConfigSpec("") {
    val material by required<String>()
    val name by optional<String?>(null)
    val lore by optional<String?>(null)
    val enchantments by optional<Map<String, Int>>(mapOf())
    val unbreakable by optional<Boolean>(false)
    val customModelData by optional<Int?>(null)
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

        val config = try {
            Config { addSpec(Spec) }.withSource(source)
        } catch (e: LoadException) {
            plugin.logger.log(Level.SEVERE, "${file.name}: ${e.message}")
            return null
        }

        try {
            config.validateRequired()
        } catch (e: UnsetValueException) {
            plugin.logger.log(Level.SEVERE, "${file.name}: ${e.message}")
            return null
        }

        return ItemTemplate(
            config[Spec.material].let { Material.valueOf(it) },
            config[Spec.name],
            config[Spec.lore],
            config[Spec.enchantments].mapKeys { (key, _) -> EnchantmentWrapper(key).enchantment },
            config[Spec.unbreakable],
            config[Spec.customModelData]
        )
    }
}
