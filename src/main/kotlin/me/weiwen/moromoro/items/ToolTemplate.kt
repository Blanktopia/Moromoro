package me.weiwen.moromoro.items

import io.papermc.paper.datacomponent.item.Tool
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.set.RegistrySet
import io.papermc.paper.registry.tag.TagKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.kyori.adventure.key.Key
import net.kyori.adventure.util.TriState
import org.bukkit.Registry

@Serializable
data class ToolTemplate(
    val rules: List<ToolTemplateRule>,
    @SerialName("default-mining-speed")
    val defaultMiningSpeed: Float? = null,
    @SerialName("damage-per-block")
    val damagePerBlock: Int? = null,
//    @SerialName("canDestroyBlocksInCreative")
//    val canDestroyBlocksInCreative: Boolean? = null,
)

fun ToolTemplate.dataComponent(): Tool {
    return Tool.tool().apply {
        defaultMiningSpeed?.let { defaultMiningSpeed(it) }
        damagePerBlock?.let { damagePerBlock(it) }
//        canDestroyBlocksInCreative?.let { canDestroyBlocksInCreative(it) }
        addRules(rules.map { it.dataComponent() })
    }.build()
}

@Serializable
data class ToolTemplateRule(
    val blocks: String,
    val speed: Float? = null,
    @SerialName("correct-for-drops")
    val correctForDrops: Boolean? = null,
)

fun ToolTemplateRule.dataComponent(): Tool.Rule {
    val keySet = if (blocks.startsWith("#")) {
        val tagKey = TagKey.create(RegistryKey.BLOCK, Key.key(blocks.substring(1)))
        Registry.BLOCK.getTag(tagKey)
    } else {
        val typedKey = TypedKey.create(RegistryKey.BLOCK, Key.key(blocks))
        RegistrySet.keySet(
            RegistryKey.BLOCK,
            typedKey
        )
    }

    return Tool.rule(
        keySet,
        speed,
        TriState.byBoolean(correctForDrops)
    )
}