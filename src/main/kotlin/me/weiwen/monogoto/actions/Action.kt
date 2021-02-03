package me.weiwen.monogoto.actions

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import me.weiwen.monogoto.Monogoto
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import java.util.logging.Level

data class Context(
    val event: Event,
    val player: Player,
    val item: ItemStack,
    val entity: Entity?,
    val block: Block?,
    val blockFace: BlockFace?,
)

fun interface Action {
    fun perform(ctx: Context): Boolean
}

internal object ActionDeserializer : StdDeserializer<Action>(Action::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Action {
        val node = p.codec.readTree<JsonNode>(p)
        Monogoto.plugin.logger.log(Level.INFO, "$node")
        val action = node.get("action")?.asText(null)
        return when (action) {
            // Flow control
            "if" ->
                IfControl(
                    p.codec.treeToValue(node.get("if"), Action::class.java),
                    node.get("then")?.toList()?.map { p.codec.treeToValue(it, Action::class.java) } ?: listOf(),
                    node.get("else")?.toList()?.map { p.codec.treeToValue(it, Action::class.java) } ?: listOf(),
                )

            // Conditions
            "and" ->
                AndCondition(
                    node.get("and")?.toList()?.map { p.codec.treeToValue(it, Action::class.java) } ?: listOf(),
                )
            "or" ->
                OrCondition(
                    node.get("or")?.toList()?.map { p.codec.treeToValue(it, Action::class.java) } ?: listOf(),
                )
            "is-in-world" ->
                IsInWorldCondition(
                    node.get("world").asText() ?: return NoopAction
                )

            // Actions
            "play-sound" ->
                PlaySoundAction(
                    node.get("sound").asText()?.let { Sound.valueOf(it) } ?: return NoopAction,
                    node.get("pitch")?.floatValue(),
                    node.get("volume")?.floatValue(),
                )
            "cycle-tool" ->
                CycleToolAction(
                    node.get("tools")?.toList()?.map { Material.valueOf(it.asText()) } ?: return NoopAction
                )
            "hammer-block" -> {
                HammerBlockAction(
                    node.get("radius")?.intValue(),
                    node.get("depth")?.intValue(),
                )
            }
            else -> NoopAction
        }
    }
}