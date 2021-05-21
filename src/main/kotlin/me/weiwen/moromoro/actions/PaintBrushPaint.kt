package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.MoromoroConfig
import me.weiwen.moromoro.extensions.canBuildAt
import me.weiwen.moromoro.extensions.color
import me.weiwen.moromoro.extensions.playSoundAt
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.*
import org.bukkit.persistence.PersistentDataType

@Serializable
@SerialName("paint-brush-paint")
object PaintBrushPaint : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player
        val item = ctx.item
        val block = ctx.block ?: return false

        if (!player.canBuildAt(block.location)) return false

        val data = item.itemMeta?.persistentDataContainer ?: return false
        val paint = data.get(NamespacedKey(Moromoro.plugin.config.namespace, "paint"), PersistentDataType.STRING)

        val colour = when (paint) {
            "WHITE" -> DyeColor.WHITE
            "ORANGE" -> DyeColor.ORANGE
            "MAGENTA" -> DyeColor.MAGENTA
            "LIGHT BLUE" -> DyeColor.LIGHT_BLUE
            "YELLOW" -> DyeColor.YELLOW
            "LIME" -> DyeColor.LIME
            "PINK" -> DyeColor.PINK
            "GRAY" -> DyeColor.GRAY
            "LIGHT GRAY" -> DyeColor.LIGHT_GRAY
            "CYAN" -> DyeColor.CYAN
            "PURPLE" -> DyeColor.PURPLE
            "BLUE" -> DyeColor.BLUE
            "BROWN" -> DyeColor.BROWN
            "GREEN" -> DyeColor.GREEN
            "RED" -> DyeColor.RED
            "BLACK" -> DyeColor.BLACK
            else -> return false
        }

        if (block.type in WOOL && WOOL_MAP[colour] != null) {
            block.type = WOOL_MAP[colour]!!
        } else if (block.type in STAINED_GLASS && STAINED_GLASS_MAP[colour] != null) {
            block.type = STAINED_GLASS_MAP[colour]!!
        } else if (block.type in STAINED_GLASS_PANE && STAINED_GLASS_PANE_MAP[colour] != null) {
            block.type = STAINED_GLASS_PANE_MAP[colour]!!
        } else if (block.type in TERRACOTTA && TERRACOTTA_MAP[colour] != null) {
            block.type = TERRACOTTA_MAP[colour]!!
        } else if (block.type in GLAZED_TERRACOTTA && GLAZED_TERRACOTTA_MAP[colour] != null) {
            block.type = GLAZED_TERRACOTTA_MAP[colour]!!
        } else if (block.type in CONCRETE && CONCRETE_MAP[colour] != null) {
            block.type = CONCRETE_MAP[colour]!!
        } else if (block.type in CONCRETE_POWDER && CONCRETE_POWDER_MAP[colour] != null) {
            block.type = CONCRETE_POWDER_MAP[colour]!!
        } else if (block.type in CARPET && CARPET_MAP[colour] != null) {
            block.type = CARPET_MAP[colour]!!
        } else {
            return false
        }

        player.playSoundAt(Sound.BLOCK_SLIME_BLOCK_PLACE, SoundCategory.BLOCKS, 1.0f, 0.5f)

        return true
    }
}

val WOOL: List<Material> = listOf(Material.WHITE_WOOL, Material.ORANGE_WOOL, Material.MAGENTA_WOOL, Material.LIGHT_BLUE_WOOL, Material.YELLOW_WOOL, Material.LIME_WOOL, Material.PINK_WOOL, Material.GRAY_WOOL, Material.LIGHT_GRAY_WOOL, Material.CYAN_WOOL, Material.PURPLE_WOOL, Material.BLUE_WOOL, Material.BROWN_WOOL, Material.GREEN_WOOL, Material.RED_WOOL, Material.BLACK_WOOL)
val WOOL_MAP: Map<DyeColor, Material> = mapOf(
    Pair(DyeColor.WHITE, Material.WHITE_WOOL),
    Pair(DyeColor.ORANGE, Material.ORANGE_WOOL),
    Pair(DyeColor.MAGENTA, Material.MAGENTA_WOOL),
    Pair(DyeColor.LIGHT_BLUE, Material.LIGHT_BLUE_WOOL),
    Pair(DyeColor.YELLOW, Material.YELLOW_WOOL),
    Pair(DyeColor.LIME, Material.LIME_WOOL),
    Pair(DyeColor.PINK, Material.PINK_WOOL),
    Pair(DyeColor.GRAY, Material.GRAY_WOOL),
    Pair(DyeColor.LIGHT_GRAY, Material.LIGHT_GRAY_WOOL),
    Pair(DyeColor.CYAN, Material.CYAN_WOOL),
    Pair(DyeColor.PURPLE, Material.PURPLE_WOOL),
    Pair(DyeColor.BLUE, Material.BLUE_WOOL),
    Pair(DyeColor.BROWN, Material.BROWN_WOOL),
    Pair(DyeColor.GREEN, Material.GREEN_WOOL),
    Pair(DyeColor.RED, Material.RED_WOOL),
    Pair(DyeColor.BLACK, Material.BLACK_WOOL)
)
val STAINED_GLASS: List<Material> = listOf(Material.GLASS, Material.WHITE_STAINED_GLASS, Material.ORANGE_STAINED_GLASS, Material.MAGENTA_STAINED_GLASS, Material.LIGHT_BLUE_STAINED_GLASS, Material.YELLOW_STAINED_GLASS, Material.LIME_STAINED_GLASS, Material.PINK_STAINED_GLASS, Material.GRAY_STAINED_GLASS, Material.LIGHT_GRAY_STAINED_GLASS, Material.CYAN_STAINED_GLASS, Material.PURPLE_STAINED_GLASS, Material.BLUE_STAINED_GLASS, Material.BROWN_STAINED_GLASS, Material.GREEN_STAINED_GLASS, Material.RED_STAINED_GLASS, Material.BLACK_STAINED_GLASS)
val STAINED_GLASS_MAP: Map<DyeColor?, Material> = mapOf(
    Pair(null, Material.GLASS),
    Pair(DyeColor.WHITE, Material.WHITE_STAINED_GLASS),
    Pair(DyeColor.ORANGE, Material.ORANGE_STAINED_GLASS),
    Pair(DyeColor.MAGENTA, Material.MAGENTA_STAINED_GLASS),
    Pair(DyeColor.LIGHT_BLUE, Material.LIGHT_BLUE_STAINED_GLASS),
    Pair(DyeColor.YELLOW, Material.YELLOW_STAINED_GLASS),
    Pair(DyeColor.LIME, Material.LIME_STAINED_GLASS),
    Pair(DyeColor.PINK, Material.PINK_STAINED_GLASS),
    Pair(DyeColor.GRAY, Material.GRAY_STAINED_GLASS),
    Pair(DyeColor.LIGHT_GRAY, Material.LIGHT_GRAY_STAINED_GLASS),
    Pair(DyeColor.CYAN, Material.CYAN_STAINED_GLASS),
    Pair(DyeColor.PURPLE, Material.PURPLE_STAINED_GLASS),
    Pair(DyeColor.BLUE, Material.BLUE_STAINED_GLASS),
    Pair(DyeColor.BROWN, Material.BROWN_STAINED_GLASS),
    Pair(DyeColor.GREEN, Material.GREEN_STAINED_GLASS),
    Pair(DyeColor.RED, Material.RED_STAINED_GLASS),
    Pair(DyeColor.BLACK, Material.BLACK_STAINED_GLASS)
)
val STAINED_GLASS_PANE: List<Material> = listOf(Material.GLASS_PANE, Material.WHITE_STAINED_GLASS_PANE, Material.ORANGE_STAINED_GLASS_PANE, Material.MAGENTA_STAINED_GLASS_PANE, Material.LIGHT_BLUE_STAINED_GLASS_PANE, Material.YELLOW_STAINED_GLASS_PANE, Material.LIME_STAINED_GLASS_PANE, Material.PINK_STAINED_GLASS_PANE, Material.GRAY_STAINED_GLASS_PANE, Material.LIGHT_GRAY_STAINED_GLASS_PANE, Material.CYAN_STAINED_GLASS_PANE, Material.PURPLE_STAINED_GLASS_PANE, Material.BLUE_STAINED_GLASS_PANE, Material.BROWN_STAINED_GLASS_PANE, Material.GREEN_STAINED_GLASS_PANE, Material.RED_STAINED_GLASS_PANE, Material.BLACK_STAINED_GLASS_PANE)
val STAINED_GLASS_PANE_MAP: Map<DyeColor?, Material> = mapOf(
    Pair(null, Material.GLASS_PANE),
    Pair(DyeColor.WHITE, Material.WHITE_STAINED_GLASS_PANE),
    Pair(DyeColor.ORANGE, Material.ORANGE_STAINED_GLASS_PANE),
    Pair(DyeColor.MAGENTA, Material.MAGENTA_STAINED_GLASS_PANE),
    Pair(DyeColor.LIGHT_BLUE, Material.LIGHT_BLUE_STAINED_GLASS_PANE),
    Pair(DyeColor.YELLOW, Material.YELLOW_STAINED_GLASS_PANE),
    Pair(DyeColor.LIME, Material.LIME_STAINED_GLASS_PANE),
    Pair(DyeColor.PINK, Material.PINK_STAINED_GLASS_PANE),
    Pair(DyeColor.GRAY, Material.GRAY_STAINED_GLASS_PANE),
    Pair(DyeColor.LIGHT_GRAY, Material.LIGHT_GRAY_STAINED_GLASS_PANE),
    Pair(DyeColor.CYAN, Material.CYAN_STAINED_GLASS_PANE),
    Pair(DyeColor.PURPLE, Material.PURPLE_STAINED_GLASS_PANE),
    Pair(DyeColor.BLUE, Material.BLUE_STAINED_GLASS_PANE),
    Pair(DyeColor.BROWN, Material.BROWN_STAINED_GLASS_PANE),
    Pair(DyeColor.GREEN, Material.GREEN_STAINED_GLASS_PANE),
    Pair(DyeColor.RED, Material.RED_STAINED_GLASS_PANE),
    Pair(DyeColor.BLACK, Material.BLACK_STAINED_GLASS_PANE)
)
val TERRACOTTA: List<Material> = listOf(Material.TERRACOTTA, Material.WHITE_TERRACOTTA, Material.ORANGE_TERRACOTTA, Material.MAGENTA_TERRACOTTA, Material.LIGHT_BLUE_TERRACOTTA, Material.YELLOW_TERRACOTTA, Material.LIME_TERRACOTTA, Material.PINK_TERRACOTTA, Material.GRAY_TERRACOTTA, Material.LIGHT_GRAY_TERRACOTTA, Material.CYAN_TERRACOTTA, Material.PURPLE_TERRACOTTA, Material.BLUE_TERRACOTTA, Material.BROWN_TERRACOTTA, Material.GREEN_TERRACOTTA, Material.RED_TERRACOTTA, Material.BLACK_TERRACOTTA)
val TERRACOTTA_MAP: Map<DyeColor?, Material> = mapOf(
    Pair(null, Material.TERRACOTTA),
    Pair(DyeColor.WHITE, Material.WHITE_TERRACOTTA),
    Pair(DyeColor.ORANGE, Material.ORANGE_TERRACOTTA),
    Pair(DyeColor.MAGENTA, Material.MAGENTA_TERRACOTTA),
    Pair(DyeColor.LIGHT_BLUE, Material.LIGHT_BLUE_TERRACOTTA),
    Pair(DyeColor.YELLOW, Material.YELLOW_TERRACOTTA),
    Pair(DyeColor.LIME, Material.LIME_TERRACOTTA),
    Pair(DyeColor.PINK, Material.PINK_TERRACOTTA),
    Pair(DyeColor.GRAY, Material.GRAY_TERRACOTTA),
    Pair(DyeColor.LIGHT_GRAY, Material.LIGHT_GRAY_TERRACOTTA),
    Pair(DyeColor.CYAN, Material.CYAN_TERRACOTTA),
    Pair(DyeColor.PURPLE, Material.PURPLE_TERRACOTTA),
    Pair(DyeColor.BLUE, Material.BLUE_TERRACOTTA),
    Pair(DyeColor.BROWN, Material.BROWN_TERRACOTTA),
    Pair(DyeColor.GREEN, Material.GREEN_TERRACOTTA),
    Pair(DyeColor.RED, Material.RED_TERRACOTTA),
    Pair(DyeColor.BLACK, Material.BLACK_TERRACOTTA)
)
val GLAZED_TERRACOTTA: List<Material> = listOf(Material.WHITE_GLAZED_TERRACOTTA, Material.ORANGE_GLAZED_TERRACOTTA, Material.MAGENTA_GLAZED_TERRACOTTA, Material.LIGHT_BLUE_GLAZED_TERRACOTTA, Material.YELLOW_GLAZED_TERRACOTTA, Material.LIME_GLAZED_TERRACOTTA, Material.PINK_GLAZED_TERRACOTTA, Material.GRAY_GLAZED_TERRACOTTA, Material.LIGHT_GRAY_GLAZED_TERRACOTTA, Material.CYAN_GLAZED_TERRACOTTA, Material.PURPLE_GLAZED_TERRACOTTA, Material.BLUE_GLAZED_TERRACOTTA, Material.BROWN_GLAZED_TERRACOTTA, Material.GREEN_GLAZED_TERRACOTTA, Material.RED_GLAZED_TERRACOTTA, Material.BLACK_GLAZED_TERRACOTTA)
val GLAZED_TERRACOTTA_MAP: Map<DyeColor, Material> = mapOf(
    Pair(DyeColor.WHITE, Material.WHITE_GLAZED_TERRACOTTA),
    Pair(DyeColor.ORANGE, Material.ORANGE_GLAZED_TERRACOTTA),
    Pair(DyeColor.MAGENTA, Material.MAGENTA_GLAZED_TERRACOTTA),
    Pair(DyeColor.LIGHT_BLUE, Material.LIGHT_BLUE_GLAZED_TERRACOTTA),
    Pair(DyeColor.YELLOW, Material.YELLOW_GLAZED_TERRACOTTA),
    Pair(DyeColor.LIME, Material.LIME_GLAZED_TERRACOTTA),
    Pair(DyeColor.PINK, Material.PINK_GLAZED_TERRACOTTA),
    Pair(DyeColor.GRAY, Material.GRAY_GLAZED_TERRACOTTA),
    Pair(DyeColor.LIGHT_GRAY, Material.LIGHT_GRAY_GLAZED_TERRACOTTA),
    Pair(DyeColor.CYAN, Material.CYAN_GLAZED_TERRACOTTA),
    Pair(DyeColor.PURPLE, Material.PURPLE_GLAZED_TERRACOTTA),
    Pair(DyeColor.BLUE, Material.BLUE_GLAZED_TERRACOTTA),
    Pair(DyeColor.BROWN, Material.BROWN_GLAZED_TERRACOTTA),
    Pair(DyeColor.GREEN, Material.GREEN_GLAZED_TERRACOTTA),
    Pair(DyeColor.RED, Material.RED_GLAZED_TERRACOTTA)
)
val CONCRETE: List<Material> = listOf(Material.WHITE_CONCRETE, Material.ORANGE_CONCRETE, Material.MAGENTA_CONCRETE, Material.LIGHT_BLUE_CONCRETE, Material.YELLOW_CONCRETE, Material.LIME_CONCRETE, Material.PINK_CONCRETE, Material.GRAY_CONCRETE, Material.LIGHT_GRAY_CONCRETE, Material.CYAN_CONCRETE, Material.PURPLE_CONCRETE, Material.BLUE_CONCRETE, Material.BROWN_CONCRETE, Material.GREEN_CONCRETE, Material.RED_CONCRETE, Material.BLACK_CONCRETE)
val CONCRETE_MAP: Map<DyeColor, Material> = mapOf(
    Pair(DyeColor.WHITE, Material.WHITE_CONCRETE),
    Pair(DyeColor.ORANGE, Material.ORANGE_CONCRETE),
    Pair(DyeColor.MAGENTA, Material.MAGENTA_CONCRETE),
    Pair(DyeColor.LIGHT_BLUE, Material.LIGHT_BLUE_CONCRETE),
    Pair(DyeColor.YELLOW, Material.YELLOW_CONCRETE),
    Pair(DyeColor.LIME, Material.LIME_CONCRETE),
    Pair(DyeColor.PINK, Material.PINK_CONCRETE),
    Pair(DyeColor.GRAY, Material.GRAY_CONCRETE),
    Pair(DyeColor.LIGHT_GRAY, Material.LIGHT_GRAY_CONCRETE),
    Pair(DyeColor.CYAN, Material.CYAN_CONCRETE),
    Pair(DyeColor.PURPLE, Material.PURPLE_CONCRETE),
    Pair(DyeColor.BLUE, Material.BLUE_CONCRETE),
    Pair(DyeColor.BROWN, Material.BROWN_CONCRETE),
    Pair(DyeColor.GREEN, Material.GREEN_CONCRETE),
    Pair(DyeColor.RED, Material.RED_CONCRETE),
    Pair(DyeColor.BLACK, Material.BLACK_CONCRETE)
)
val CONCRETE_POWDER: List<Material> = listOf(Material.WHITE_CONCRETE_POWDER, Material.ORANGE_CONCRETE_POWDER, Material.MAGENTA_CONCRETE_POWDER, Material.LIGHT_BLUE_CONCRETE_POWDER, Material.YELLOW_CONCRETE_POWDER, Material.LIME_CONCRETE_POWDER, Material.PINK_CONCRETE_POWDER, Material.GRAY_CONCRETE_POWDER, Material.LIGHT_GRAY_CONCRETE_POWDER, Material.CYAN_CONCRETE_POWDER, Material.PURPLE_CONCRETE_POWDER, Material.BLUE_CONCRETE_POWDER, Material.BROWN_CONCRETE_POWDER, Material.GREEN_CONCRETE_POWDER, Material.RED_CONCRETE_POWDER, Material.BLACK_CONCRETE_POWDER)
val CONCRETE_POWDER_MAP: Map<DyeColor, Material> = mapOf(
    Pair(DyeColor.WHITE, Material.WHITE_CONCRETE_POWDER),
    Pair(DyeColor.ORANGE, Material.ORANGE_CONCRETE_POWDER),
    Pair(DyeColor.MAGENTA, Material.MAGENTA_CONCRETE_POWDER),
    Pair(DyeColor.LIGHT_BLUE, Material.LIGHT_BLUE_CONCRETE_POWDER),
    Pair(DyeColor.YELLOW, Material.YELLOW_CONCRETE_POWDER),
    Pair(DyeColor.LIME, Material.LIME_CONCRETE_POWDER),
    Pair(DyeColor.PINK, Material.PINK_CONCRETE_POWDER),
    Pair(DyeColor.GRAY, Material.GRAY_CONCRETE_POWDER),
    Pair(DyeColor.LIGHT_GRAY, Material.LIGHT_GRAY_CONCRETE_POWDER),
    Pair(DyeColor.CYAN, Material.CYAN_CONCRETE_POWDER),
    Pair(DyeColor.PURPLE, Material.PURPLE_CONCRETE_POWDER),
    Pair(DyeColor.BLUE, Material.BLUE_CONCRETE_POWDER),
    Pair(DyeColor.BROWN, Material.BROWN_CONCRETE_POWDER),
    Pair(DyeColor.GREEN, Material.GREEN_CONCRETE_POWDER),
    Pair(DyeColor.RED, Material.RED_CONCRETE_POWDER),
    Pair(DyeColor.BLACK, Material.BLACK_CONCRETE_POWDER)
)
val CARPET: List<Material> = listOf(Material.WHITE_CARPET, Material.ORANGE_CARPET, Material.MAGENTA_CARPET, Material.LIGHT_BLUE_CARPET, Material.YELLOW_CARPET, Material.LIME_CARPET, Material.PINK_CARPET, Material.GRAY_CARPET, Material.LIGHT_GRAY_CARPET, Material.CYAN_CARPET, Material.PURPLE_CARPET, Material.BLUE_CARPET, Material.BROWN_CARPET, Material.GREEN_CARPET, Material.RED_CARPET, Material.BLACK_CARPET)
val CARPET_MAP: Map<DyeColor, Material> = mapOf(
    Pair(DyeColor.WHITE, Material.WHITE_CARPET),
    Pair(DyeColor.ORANGE, Material.ORANGE_CARPET),
    Pair(DyeColor.MAGENTA, Material.MAGENTA_CARPET),
    Pair(DyeColor.LIGHT_BLUE, Material.LIGHT_BLUE_CARPET),
    Pair(DyeColor.YELLOW, Material.YELLOW_CARPET),
    Pair(DyeColor.LIME, Material.LIME_CARPET),
    Pair(DyeColor.PINK, Material.PINK_CARPET),
    Pair(DyeColor.GRAY, Material.GRAY_CARPET),
    Pair(DyeColor.LIGHT_GRAY, Material.LIGHT_GRAY_CARPET),
    Pair(DyeColor.CYAN, Material.CYAN_CARPET),
    Pair(DyeColor.PURPLE, Material.PURPLE_CARPET),
    Pair(DyeColor.BLUE, Material.BLUE_CARPET),
    Pair(DyeColor.BROWN, Material.BROWN_CARPET),
    Pair(DyeColor.GREEN, Material.GREEN_CARPET),
    Pair(DyeColor.RED, Material.RED_CARPET),
    Pair(DyeColor.BLACK, Material.BLACK_CARPET)
)

