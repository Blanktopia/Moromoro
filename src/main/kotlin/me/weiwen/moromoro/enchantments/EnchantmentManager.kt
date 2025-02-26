package me.weiwen.moromoro.items

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.data.EnchantmentRegistryEntry
import io.papermc.paper.registry.event.RegistryEvents
import io.papermc.paper.registry.keys.EnchantmentKeys
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import io.papermc.paper.registry.tag.TagKey
import io.papermc.paper.tag.PostFlattenTagRegistrar
import io.papermc.paper.tag.PreFlattenTagRegistrar
import io.papermc.paper.tag.TagEntry
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.weiwen.moromoro.Manager
import me.weiwen.moromoro.MoromoroConfig
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Trigger
import me.weiwen.moromoro.actions.actionModule
import me.weiwen.moromoro.serializers.component
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemType
import java.io.File
import java.util.stream.Collectors

object EnchantmentManager: Manager {
    lateinit var logger: ComponentLogger

    var keys: Set<String> = setOf()
        private set
    var templates: MutableMap<String, EnchantmentTemplate> = mutableMapOf()
        private set
    var triggers: MutableMap<String, Map<Trigger, List<Action>>> = mutableMapOf()
        private set

    var primaryItemsTag: MutableMap<String, TagKey<ItemType>> = mutableMapOf()
    var supportedItemsTag: MutableMap<String, TagKey<ItemType>> = mutableMapOf()
    var exclusiveEnchantmentsTag: MutableMap<String, TagKey<Enchantment>> = mutableMapOf()

    fun bootstrap(context: BootstrapContext, config: MoromoroConfig) {
        this.logger = context.logger
        val dataFolder = context.dataDirectory.toFile()

        load(dataFolder)

        for ((key, _) in templates) {
            primaryItemsTag[key] = TagKey.create(RegistryKey.ITEM, Key.key(config.namespace, "enchantable/${key}"))
            supportedItemsTag[key] = TagKey.create(RegistryKey.ITEM, Key.key(config.namespace, "enchantable/supported/${key}"))
            exclusiveEnchantmentsTag[key] = TagKey.create(RegistryKey.ENCHANTMENT, Key.key(config.namespace, "exclusive_set/${key}"))
        }

        context.lifecycleManager.registerEventHandler(RegistryEvents.ENCHANTMENT.freeze().newHandler { event ->
            for ((key, template) in templates) {
                event.registry().register(EnchantmentKeys.create(Key.key(config.namespace, key))) { b ->
                    template.name?.component?.let { b.description(it) }
                    b.anvilCost(template.anvilCost)
                        .maxLevel(template.maxLevel)
                        .weight(template.weight)
                        .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(
                            template.minimumCostBase,
                            template.minimumCostPerLevel
                        ))
                        .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(
                            template.maximumCostBase ?: template.minimumCostBase,
                            template.maximumCostPerLevel ?: template.minimumCostPerLevel
                        ))
                        .primaryItems(event.getOrCreateTag(primaryItemsTag[key]!!))
                        .supportedItems(event.getOrCreateTag(supportedItemsTag[key]!!))
                        .exclusiveWith(event.getOrCreateTag(exclusiveEnchantmentsTag[key]!!))
                        .activeSlots(template.slots)
                }
            }
        })

        context.lifecycleManager.registerEventHandler(LifecycleEvents.TAGS.preFlatten(RegistryKey.ENCHANTMENT).newHandler { event ->
            event.registrar().addToTag(
                EnchantmentTagKeys.CURSE,
                templates.entries.stream()
                    .filter { it.value.curse }
                    .map { TagEntry.valueEntry(TypedKey.create(RegistryKey.ENCHANTMENT, Key.key(config.namespace, it.key))) }
                    .collect(Collectors.toSet())
            )

            event.registrar().addToTag(
               EnchantmentTagKeys.TREASURE,
               templates.entries.stream()
                   .filter { it.value.treasure }
                   .map { TagEntry.valueEntry(TypedKey.create(RegistryKey.ENCHANTMENT, Key.key(config.namespace, it.key))) }
                   .collect(Collectors.toSet())
            )

            event.registrar().addToTag(
                EnchantmentTagKeys.NON_TREASURE,
                templates.entries.stream()
                    .filter { !it.value.treasure }
                    .map { TagEntry.valueEntry(TypedKey.create(RegistryKey.ENCHANTMENT, Key.key(config.namespace, it.key))) }
                    .collect(Collectors.toSet())
            )

            event.registrar().addToTag(
                EnchantmentTagKeys.SMELTS_LOOT,
                templates.entries.stream()
                    .filter { it.value.smeltsLoot }
                    .map { TagEntry.valueEntry(TypedKey.create(RegistryKey.ENCHANTMENT, Key.key(config.namespace, it.key))) }
                    .collect(Collectors.toSet())
            )
        })

        context.lifecycleManager.registerEventHandler(LifecycleEvents.TAGS.postFlatten(RegistryKey.ENCHANTMENT).newHandler { event ->
            for ((key, template) in templates) {
                val exclusiveEnchantments = exclusiveEnchantmentsTag.get(key) ?: continue
                event.registrar().addToTag(exclusiveEnchantments, enchantmentKeySet(event.registrar(), template.exclusiveWith))
            }
        })

        context.lifecycleManager.registerEventHandler(LifecycleEvents.TAGS.preFlatten(RegistryKey.ITEM).newHandler { event ->
            for ((key, template) in templates) {
                val primaryItems = primaryItemsTag.get(key) ?: continue
                event.registrar().addToTag(primaryItems, itemKeySet(event.registrar(), template.primaryItems))
                val supportedItems = supportedItemsTag.get(key) ?: continue
                event.registrar().addToTag(supportedItems, itemKeySet(event.registrar(), template.supportedItems ?: template.primaryItems))
            }
        })
    }

    private fun enchantmentKeySet(registrar: PostFlattenTagRegistrar<Enchantment>, keys: List<String>): Set<TypedKey<Enchantment>> {
        val enchantments: MutableSet<TypedKey<Enchantment>> = mutableSetOf()
        for (key in keys) {
            if (key.startsWith("#")) {
                val tagKey = TagKey.create(RegistryKey.ENCHANTMENT, Key.key(key.substring(1)))
                val tag = registrar.getTag(tagKey)
                enchantments.addAll(tag)
            } else {
                val typedKey = TypedKey.create(RegistryKey.ENCHANTMENT, Key.key(key))
                enchantments.add(typedKey)
            }
        }
        return enchantments
    }

    private fun itemKeySet(registrar: PreFlattenTagRegistrar<ItemType>, keys: List<String>): Set<TagEntry<ItemType>> {
        val items: MutableSet<TagEntry<ItemType>> = mutableSetOf()
        for (key in keys) {
            if (key.startsWith("#")) {
                val tagKey = TagKey.create(RegistryKey.ITEM, Key.key(key.substring(1)))
                val tag = TagEntry.tagEntry(tagKey)
                items.add(tag)
            } else {
                val typedKey = TypedKey.create(RegistryKey.ITEM, Key.key(key))
                val tag = TagEntry.valueEntry(typedKey)
                items.add(tag)
            }
        }
        return items
    }

    private fun load(dataFolder: File) {
        val directory = File(dataFolder, "enchantments")

        if (!directory.isDirectory) {
            directory.mkdirs()
        }

        triggers.clear()
        templates.clear()

        // We process files closer to the root first, so we can resolve dependency issues with nesting
        val files = directory
            .walkBottomUp()
            .onEnter { !it.name.startsWith("_") }
            .filter { file -> file.extension in setOf("json", "yml", "yaml") }
            .sortedBy { it.toPath().nameCount }
        for (file in files) {
            val template = parse(file) ?: continue
            templates[file.nameWithoutExtension] = template
        }

        keys = templates.keys
    }

    private val json = Json {
        serializersModule = actionModule
    }

    private val yaml = Yaml(
        actionModule,
        YamlConfiguration(
            polymorphismStyle = PolymorphismStyle.Property
        )
    )

    private fun parse(file: File): EnchantmentTemplate? {
        val key = file.nameWithoutExtension

        val format = when (file.extension) {
            "json" -> json
            "yml", "yaml" -> yaml
            else -> return null
        }

        val text = file.readText()
        val template = try {
            format.decodeFromString<EnchantmentTemplate>(text)
        } catch (e: Exception) {
            logger.error("Error parsing '${file.name}': ${e.message}")
            return null
        }

        return template
    }
}