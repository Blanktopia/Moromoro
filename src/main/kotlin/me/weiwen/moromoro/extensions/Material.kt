package me.weiwen.moromoro.extensions

import org.bukkit.DyeColor
import org.bukkit.DyeColor.*
import org.bukkit.Material
import org.bukkit.Material.*
import org.bukkit.Tag

val Material.isPickaxe: Boolean
    get() = when (this) {
        WOODEN_PICKAXE,
        STONE_PICKAXE,
        IRON_PICKAXE,
        GOLDEN_PICKAXE,
        DIAMOND_PICKAXE,
        NETHERITE_PICKAXE -> true
        else -> false
    }

val Material.isAxe: Boolean
    get() = when (this) {
        WOODEN_AXE,
        STONE_AXE,
        IRON_AXE,
        GOLDEN_AXE,
        DIAMOND_AXE,
        NETHERITE_AXE -> true
        else -> false
    }

val Material.isShovel: Boolean
    get() = when (this) {
        WOODEN_SHOVEL,
        STONE_SHOVEL,
        IRON_SHOVEL,
        GOLDEN_SHOVEL,
        DIAMOND_SHOVEL,
        NETHERITE_SHOVEL -> true
        else -> false
    }

val Material.isHoe: Boolean
    get() = when (this) {
        WOODEN_HOE,
        STONE_HOE,
        IRON_HOE,
        GOLDEN_HOE,
        DIAMOND_HOE,
        NETHERITE_HOE -> true
        else -> false
    }

val Material.isShulker: Boolean
    get() = when (this) {
        SHULKER_BOX,
        WHITE_SHULKER_BOX,
        ORANGE_SHULKER_BOX,
        MAGENTA_SHULKER_BOX,
        LIGHT_BLUE_SHULKER_BOX,
        YELLOW_SHULKER_BOX,
        LIME_SHULKER_BOX,
        PINK_SHULKER_BOX,
        GRAY_SHULKER_BOX,
        LIGHT_GRAY_SHULKER_BOX,
        CYAN_SHULKER_BOX,
        PURPLE_SHULKER_BOX,
        BLUE_SHULKER_BOX,
        BROWN_SHULKER_BOX,
        GREEN_SHULKER_BOX,
        RED_SHULKER_BOX,
        BLACK_SHULKER_BOX -> true
        else -> false
    }

val Material.isPartial: Boolean
    get() = when (this) {
        CHEST,
        TRAPPED_CHEST,
        WHITE_BED,
        ORANGE_BED,
        MAGENTA_BED,
        LIGHT_BLUE_BED,
        YELLOW_BED,
        LIME_BED,
        PINK_BED,
        GRAY_BED,
        LIGHT_GRAY_BED,
        CYAN_BED,
        PURPLE_BED,
        BLUE_BED,
        BROWN_BED,
        GREEN_BED,
        RED_BED,
        BLACK_BED,
        SUNFLOWER,
        LILAC,
        ROSE_BUSH,
        PEONY,
        TALL_GRASS,
        LARGE_FERN,
        IRON_DOOR,
        OAK_DOOR,
        SPRUCE_DOOR,
        BIRCH_DOOR,
        JUNGLE_DOOR,
        ACACIA_DOOR,
        DARK_OAK_DOOR,
        WARPED_DOOR,
        CRIMSON_DOOR,
        END_PORTAL_FRAME -> true
        else -> false
    }

val Material.isPartiallyEmpty: Boolean
    get() = when (this) {
        CAVE_AIR,
        AIR,
        WATER,
        LAVA,
        SHORT_GRASS,
        TALL_GRASS,
        FERN,
        LARGE_FERN,
        SNOW -> true
        else -> false
    }

val Material.stripped: Material?
    get() = when (this) {
        OAK_LOG -> STRIPPED_OAK_LOG
        OAK_WOOD -> STRIPPED_OAK_WOOD
        SPRUCE_LOG -> STRIPPED_SPRUCE_LOG
        SPRUCE_WOOD -> STRIPPED_SPRUCE_WOOD
        BIRCH_LOG -> STRIPPED_BIRCH_LOG
        BIRCH_WOOD -> STRIPPED_BIRCH_WOOD
        JUNGLE_LOG -> STRIPPED_JUNGLE_LOG
        JUNGLE_WOOD -> STRIPPED_JUNGLE_WOOD
        ACACIA_LOG -> STRIPPED_ACACIA_LOG
        ACACIA_WOOD -> STRIPPED_ACACIA_WOOD
        DARK_OAK_LOG -> STRIPPED_DARK_OAK_LOG
        DARK_OAK_WOOD -> STRIPPED_DARK_OAK_WOOD
        CRIMSON_STEM -> STRIPPED_CRIMSON_STEM
        CRIMSON_HYPHAE -> STRIPPED_CRIMSON_HYPHAE
        WARPED_STEM -> STRIPPED_WARPED_STEM
        WARPED_HYPHAE -> STRIPPED_WARPED_HYPHAE
        else -> null
    }

val Material.unstripped: Material?
    get() = when (this) {
        STRIPPED_OAK_LOG -> OAK_LOG
        STRIPPED_OAK_WOOD -> OAK_WOOD
        STRIPPED_SPRUCE_LOG -> SPRUCE_LOG
        STRIPPED_SPRUCE_WOOD -> SPRUCE_WOOD
        STRIPPED_BIRCH_LOG -> BIRCH_LOG
        STRIPPED_BIRCH_WOOD -> BIRCH_WOOD
        STRIPPED_JUNGLE_LOG -> JUNGLE_LOG
        STRIPPED_JUNGLE_WOOD -> JUNGLE_WOOD
        STRIPPED_ACACIA_LOG -> ACACIA_LOG
        STRIPPED_ACACIA_WOOD -> ACACIA_WOOD
        STRIPPED_DARK_OAK_LOG -> DARK_OAK_LOG
        STRIPPED_DARK_OAK_WOOD -> DARK_OAK_WOOD
        STRIPPED_CRIMSON_STEM -> CRIMSON_STEM
        STRIPPED_CRIMSON_HYPHAE -> CRIMSON_HYPHAE
        STRIPPED_WARPED_STEM -> WARPED_STEM
        STRIPPED_WARPED_HYPHAE -> WARPED_HYPHAE
        else -> null
    }

val Material.color: DyeColor?
    get() = when (this) {
        WHITE_WOOL -> WHITE
        WHITE_STAINED_GLASS -> WHITE
        WHITE_STAINED_GLASS_PANE -> WHITE
        WHITE_TERRACOTTA -> WHITE
        WHITE_GLAZED_TERRACOTTA -> WHITE
        WHITE_CONCRETE -> WHITE
        WHITE_CONCRETE_POWDER -> WHITE
        WHITE_BED -> WHITE
        WHITE_CARPET -> WHITE
        WHITE_SHULKER_BOX -> WHITE
        WHITE_CANDLE -> WHITE

        ORANGE_WOOL -> ORANGE
        ORANGE_STAINED_GLASS -> ORANGE
        ORANGE_STAINED_GLASS_PANE -> ORANGE
        ORANGE_TERRACOTTA -> ORANGE
        ORANGE_GLAZED_TERRACOTTA -> ORANGE
        ORANGE_CONCRETE -> ORANGE
        ORANGE_CONCRETE_POWDER -> ORANGE
        ORANGE_BED -> ORANGE
        ORANGE_CARPET -> ORANGE
        ORANGE_SHULKER_BOX -> ORANGE
        ORANGE_CANDLE -> ORANGE

        MAGENTA_WOOL -> MAGENTA
        MAGENTA_STAINED_GLASS -> MAGENTA
        MAGENTA_STAINED_GLASS_PANE -> MAGENTA
        MAGENTA_TERRACOTTA -> MAGENTA
        MAGENTA_GLAZED_TERRACOTTA -> MAGENTA
        MAGENTA_CONCRETE -> MAGENTA
        MAGENTA_CONCRETE_POWDER -> MAGENTA
        MAGENTA_BED -> MAGENTA
        MAGENTA_CARPET -> MAGENTA
        MAGENTA_SHULKER_BOX -> MAGENTA
        MAGENTA_CANDLE -> MAGENTA

        LIGHT_BLUE_WOOL -> LIGHT_BLUE
        LIGHT_BLUE_STAINED_GLASS -> LIGHT_BLUE
        LIGHT_BLUE_STAINED_GLASS_PANE -> LIGHT_BLUE
        LIGHT_BLUE_TERRACOTTA -> LIGHT_BLUE
        LIGHT_BLUE_GLAZED_TERRACOTTA -> LIGHT_BLUE
        LIGHT_BLUE_CONCRETE -> LIGHT_BLUE
        LIGHT_BLUE_CONCRETE_POWDER -> LIGHT_BLUE
        LIGHT_BLUE_BED -> LIGHT_BLUE
        LIGHT_BLUE_CARPET -> LIGHT_BLUE
        LIGHT_BLUE_SHULKER_BOX -> LIGHT_BLUE
        LIGHT_BLUE_CANDLE -> LIGHT_BLUE

        YELLOW_WOOL -> YELLOW
        YELLOW_STAINED_GLASS -> YELLOW
        YELLOW_STAINED_GLASS_PANE -> YELLOW
        YELLOW_TERRACOTTA -> YELLOW
        YELLOW_GLAZED_TERRACOTTA -> YELLOW
        YELLOW_CONCRETE -> YELLOW
        YELLOW_CONCRETE_POWDER -> YELLOW
        YELLOW_BED -> YELLOW
        YELLOW_CARPET -> YELLOW
        YELLOW_SHULKER_BOX -> YELLOW
        YELLOW_CANDLE -> YELLOW

        LIME_WOOL -> LIME
        LIME_STAINED_GLASS -> LIME
        LIME_STAINED_GLASS_PANE -> LIME
        LIME_TERRACOTTA -> LIME
        LIME_GLAZED_TERRACOTTA -> LIME
        LIME_CONCRETE -> LIME
        LIME_CONCRETE_POWDER -> LIME
        LIME_BED -> LIME
        LIME_CARPET -> LIME
        LIME_SHULKER_BOX -> LIME
        LIME_CANDLE -> LIME

        PINK_WOOL -> PINK
        PINK_STAINED_GLASS -> PINK
        PINK_STAINED_GLASS_PANE -> PINK
        PINK_TERRACOTTA -> PINK
        PINK_GLAZED_TERRACOTTA -> PINK
        PINK_CONCRETE -> PINK
        PINK_CONCRETE_POWDER -> PINK
        PINK_BED -> PINK
        PINK_CARPET -> PINK
        PINK_SHULKER_BOX -> PINK
        PINK_CANDLE -> PINK

        GRAY_WOOL -> GRAY
        GRAY_STAINED_GLASS -> GRAY
        GRAY_STAINED_GLASS_PANE -> GRAY
        GRAY_TERRACOTTA -> GRAY
        GRAY_GLAZED_TERRACOTTA -> GRAY
        GRAY_CONCRETE -> GRAY
        GRAY_CONCRETE_POWDER -> GRAY
        GRAY_BED -> GRAY
        GRAY_CARPET -> GRAY
        GRAY_SHULKER_BOX -> GRAY
        GRAY_CANDLE -> GRAY

        LIGHT_GRAY_WOOL -> LIGHT_GRAY
        LIGHT_GRAY_STAINED_GLASS -> LIGHT_GRAY
        LIGHT_GRAY_STAINED_GLASS_PANE -> LIGHT_GRAY
        LIGHT_GRAY_TERRACOTTA -> LIGHT_GRAY
        LIGHT_GRAY_GLAZED_TERRACOTTA -> LIGHT_GRAY
        LIGHT_GRAY_CONCRETE -> LIGHT_GRAY
        LIGHT_GRAY_CONCRETE_POWDER -> LIGHT_GRAY
        LIGHT_GRAY_BED -> LIGHT_GRAY
        LIGHT_GRAY_CARPET -> LIGHT_GRAY
        LIGHT_GRAY_SHULKER_BOX -> LIGHT_GRAY
        LIGHT_GRAY_CANDLE -> LIGHT_GRAY

        CYAN_WOOL -> CYAN
        CYAN_STAINED_GLASS -> CYAN
        CYAN_STAINED_GLASS_PANE -> CYAN
        CYAN_TERRACOTTA -> CYAN
        CYAN_GLAZED_TERRACOTTA -> CYAN
        CYAN_CONCRETE -> CYAN
        CYAN_CONCRETE_POWDER -> CYAN
        CYAN_BED -> CYAN
        CYAN_CARPET -> CYAN
        CYAN_SHULKER_BOX -> CYAN
        CYAN_CANDLE -> CYAN

        PURPLE_WOOL -> PURPLE
        PURPLE_STAINED_GLASS -> PURPLE
        PURPLE_STAINED_GLASS_PANE -> PURPLE
        PURPLE_TERRACOTTA -> PURPLE
        PURPLE_GLAZED_TERRACOTTA -> PURPLE
        PURPLE_CONCRETE -> PURPLE
        PURPLE_CONCRETE_POWDER -> PURPLE
        PURPLE_BED -> PURPLE
        PURPLE_CARPET -> PURPLE
        PURPLE_SHULKER_BOX -> PURPLE
        PURPLE_CANDLE -> PURPLE

        BLUE_WOOL -> BLUE
        BLUE_STAINED_GLASS -> BLUE
        BLUE_STAINED_GLASS_PANE -> BLUE
        BLUE_TERRACOTTA -> BLUE
        BLUE_GLAZED_TERRACOTTA -> BLUE
        BLUE_CONCRETE -> BLUE
        BLUE_CONCRETE_POWDER -> BLUE
        BLUE_BED -> BLUE
        BLUE_CARPET -> BLUE
        BLUE_SHULKER_BOX -> BLUE
        BLUE_CANDLE -> BLUE

        BROWN_WOOL -> BROWN
        BROWN_STAINED_GLASS -> BROWN
        BROWN_STAINED_GLASS_PANE -> BROWN
        BROWN_TERRACOTTA -> BROWN
        BROWN_GLAZED_TERRACOTTA -> BROWN
        BROWN_CONCRETE -> BROWN
        BROWN_CONCRETE_POWDER -> BROWN
        BROWN_BED -> BROWN
        BROWN_CARPET -> BROWN
        BROWN_SHULKER_BOX -> BROWN
        BROWN_CANDLE -> BROWN

        GREEN_WOOL -> GREEN
        GREEN_STAINED_GLASS -> GREEN
        GREEN_STAINED_GLASS_PANE -> GREEN
        GREEN_TERRACOTTA -> GREEN
        GREEN_GLAZED_TERRACOTTA -> GREEN
        GREEN_CONCRETE -> GREEN
        GREEN_CONCRETE_POWDER -> GREEN
        GREEN_BED -> GREEN
        GREEN_CARPET -> GREEN
        GREEN_SHULKER_BOX -> GREEN
        GREEN_CANDLE -> GREEN

        RED_WOOL -> RED
        RED_STAINED_GLASS -> RED
        RED_STAINED_GLASS_PANE -> RED
        RED_TERRACOTTA -> RED
        RED_GLAZED_TERRACOTTA -> RED
        RED_CONCRETE -> RED
        RED_CONCRETE_POWDER -> RED
        RED_BED -> RED
        RED_CARPET -> RED
        RED_SHULKER_BOX -> RED
        RED_CANDLE -> RED

        BLACK_WOOL -> BLACK
        BLACK_STAINED_GLASS -> BLACK
        BLACK_STAINED_GLASS_PANE -> BLACK
        BLACK_TERRACOTTA -> BLACK
        BLACK_GLAZED_TERRACOTTA -> BLACK
        BLACK_CONCRETE -> BLACK
        BLACK_CONCRETE_POWDER -> BLACK
        BLACK_BED -> BLACK
        BLACK_CARPET -> BLACK
        BLACK_SHULKER_BOX -> BLACK
        BLACK_CANDLE -> BLACK

        else -> null
    }

val Material.isReallyInteractable
    get() = when {
        this == PISTON_HEAD || Tag.STAIRS.isTagged(this) || Tag.FENCES.isTagged(this) -> false
        else -> isInteractable
    }

val netheriteTools: Set<Material> = setOf(
    Material.NETHERITE_PICKAXE,
    Material.NETHERITE_AXE,
    Material.NETHERITE_SHOVEL,
    Material.NETHERITE_HOE,
)

val goldTools: Set<Material> = setOf(
    Material.GOLDEN_PICKAXE,
    Material.GOLDEN_AXE,
    Material.GOLDEN_SHOVEL,
    Material.GOLDEN_HOE,
)

val diamondTools: Set<Material> = setOf(
    Material.DIAMOND_PICKAXE,
    Material.DIAMOND_AXE,
    Material.DIAMOND_SHOVEL,
    Material.DIAMOND_HOE,
)

val ironTools: Set<Material> = setOf(
    Material.IRON_PICKAXE,
    Material.IRON_AXE,
    Material.IRON_SHOVEL,
    Material.IRON_HOE,
)

val stoneTools: Set<Material> = setOf(
    Material.STONE_PICKAXE,
    Material.STONE_AXE,
    Material.STONE_SHOVEL,
    Material.STONE_HOE,
)

val woodenTools: Set<Material> = setOf(
    Material.WOODEN_PICKAXE,
    Material.WOODEN_AXE,
    Material.WOODEN_SHOVEL,
    Material.WOODEN_HOE,
)

val pickaxes: Set<Material> = setOf(
    Material.DIAMOND_PICKAXE,
    Material.GOLDEN_PICKAXE,
    Material.IRON_PICKAXE,
    Material.STONE_PICKAXE,
    Material.WOODEN_PICKAXE,
    Material.NETHERITE_PICKAXE
)

val axes: Set<Material> = setOf(
    Material.DIAMOND_AXE,
    Material.GOLDEN_AXE,
    Material.IRON_AXE,
    Material.STONE_AXE,
    Material.WOODEN_AXE,
    Material.NETHERITE_AXE
)

val shovels: Set<Material> = setOf(
    Material.DIAMOND_SHOVEL,
    Material.GOLDEN_SHOVEL,
    Material.IRON_SHOVEL,
    Material.STONE_SHOVEL,
    Material.WOODEN_SHOVEL,
    Material.NETHERITE_SHOVEL
)

val hoes: Set<Material> = setOf(
    Material.DIAMOND_HOE,
    Material.GOLDEN_HOE,
    Material.IRON_HOE,
    Material.STONE_HOE,
    Material.WOODEN_HOE,
    Material.NETHERITE_HOE
)

val shulkerBoxes: Set<Material> by lazy {
    Tag.SHULKER_BOXES.values
}
