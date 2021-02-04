package me.weiwen.moromoro

import com.fasterxml.jackson.databind.module.SimpleModule
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
import com.uchuhimo.konf.UnsetValueException
import com.uchuhimo.konf.source.LoadException
import com.uchuhimo.konf.source.Source
import com.uchuhimo.konf.source.toml
import com.uchuhimo.konf.source.yaml
import de.themoep.minedown.MineDown
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.ActionDeserializer
import me.weiwen.moromoro.actions.NoopAction
import me.weiwen.moromoro.actions.Trigger
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Material
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
    val triggers by optional<Map<String, List<Action>>>(mapOf())
}

class ItemParser(private val plugin: Moromoro) {
    fun parse(file: File): ItemTemplate? {
        plugin.logger.log(Level.INFO, "Parsing ${file.name}")

        val source = when (file.extension) {
            "toml" -> Source.from.toml.file(file, false)
            "json" -> Source.from.json.file(file, false)
            "yaml" -> Source.from.yaml.file(file, false)
            else -> return null
        }

        val config = try {
            Config {
                addSpec(Spec)
                mapper.registerModule(SimpleModule().addDeserializer(Action::class.java, ActionDeserializer))
            }.withSource(source)
        } catch (e: LoadException) {
            plugin.logger.log(Level.SEVERE, "${file.name}: ${e.message} (${e.cause?.message})")
            e.printStackTrace()
            return null
        }

        try {
            config.validateRequired()
        } catch (e: UnsetValueException) {
            plugin.logger.log(Level.SEVERE, "${file.name}: ${e.message} (${e.cause?.message})")
            e.printStackTrace()
            return null
        }

        val key = file.nameWithoutExtension

        config[Spec.triggers].forEach { (trigger, actions) ->
            if (actions.any { it == NoopAction }) {
                plugin.logger.log(Level.WARNING, "failed to parse action in trigger '$trigger'")
            }
        }

        val triggers = config[Spec.triggers].mapKeys { (key, _) -> Trigger.parse(key) }
        plugin.itemManager.registerTriggers(key, triggers)

        return ItemTemplate(
            key,
            config[Spec.material].let { Material.valueOf(it) },
            config[Spec.name].let { TextComponent.toLegacyText(*MineDown.parse(it)) },
            config[Spec.lore].let { TextComponent.toLegacyText(*MineDown.parse(it)) },
            config[Spec.enchantments].mapKeys { (key, _) -> EnchantmentWrapper(key).enchantment },
            config[Spec.unbreakable],
            config[Spec.customModelData]
        )
    }
}
