@file:UseSerializers(BiomeSerializer::class)

package me.weiwen.moromoro.actions.mechanic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.extensions.isPartial
import me.weiwen.moromoro.extensions.spawnParticle
import me.weiwen.moromoro.serializers.BiomeSerializer
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.BlockFace
import org.bukkit.block.data.*
import org.bukkit.block.data.type.Door

val saplings = listOf(
    Material.OAK_SAPLING,
    Material.SPRUCE_SAPLING,
    Material.BIRCH_SAPLING,
    Material.JUNGLE_SAPLING,
    Material.ACACIA_SAPLING,
    Material.DARK_OAK_SAPLING,
    Material.MANGROVE_PROPAGULE,
)

val planks = listOf(
    Material.OAK_PLANKS,
    Material.SPRUCE_PLANKS,
    Material.BIRCH_PLANKS,
    Material.JUNGLE_PLANKS,
    Material.ACACIA_PLANKS,
    Material.DARK_OAK_PLANKS,
    Material.MANGROVE_PLANKS,
    Material.CRIMSON_PLANKS,
    Material.WARPED_PLANKS,
)

val slabs = listOf(
    Material.OAK_SLAB,
    Material.SPRUCE_SLAB,
    Material.BIRCH_SLAB,
    Material.JUNGLE_SLAB,
    Material.ACACIA_SLAB,
    Material.DARK_OAK_SLAB,
    Material.MANGROVE_SLAB,
    Material.CRIMSON_SLAB,
    Material.WARPED_SLAB,
)

val stairs = listOf(
    Material.OAK_STAIRS,
    Material.SPRUCE_STAIRS,
    Material.BIRCH_STAIRS,
    Material.JUNGLE_STAIRS,
    Material.ACACIA_STAIRS,
    Material.DARK_OAK_STAIRS,
    Material.MANGROVE_STAIRS,
    Material.CRIMSON_STAIRS,
    Material.WARPED_STAIRS,
)

val fences = listOf(
    Material.OAK_FENCE,
    Material.SPRUCE_FENCE,
    Material.BIRCH_FENCE,
    Material.JUNGLE_FENCE,
    Material.ACACIA_FENCE,
    Material.DARK_OAK_FENCE,
    Material.MANGROVE_FENCE,
    Material.CRIMSON_FENCE,
    Material.WARPED_FENCE,
)

val fenceGates = listOf(
    Material.OAK_FENCE_GATE,
    Material.SPRUCE_FENCE_GATE,
    Material.BIRCH_FENCE_GATE,
    Material.JUNGLE_FENCE_GATE,
    Material.ACACIA_FENCE_GATE,
    Material.DARK_OAK_FENCE_GATE,
    Material.MANGROVE_FENCE_GATE,
    Material.CRIMSON_FENCE_GATE,
    Material.WARPED_FENCE_GATE,
)

val pressurePlates = listOf(
    Material.OAK_PRESSURE_PLATE,
    Material.SPRUCE_PRESSURE_PLATE,
    Material.BIRCH_PRESSURE_PLATE,
    Material.JUNGLE_PRESSURE_PLATE,
    Material.ACACIA_PRESSURE_PLATE,
    Material.DARK_OAK_PRESSURE_PLATE,
    Material.MANGROVE_PRESSURE_PLATE,
    Material.CRIMSON_PRESSURE_PLATE,
    Material.WARPED_PRESSURE_PLATE,
)

val buttons = listOf(
    Material.OAK_BUTTON,
    Material.SPRUCE_BUTTON,
    Material.BIRCH_BUTTON,
    Material.JUNGLE_BUTTON,
    Material.ACACIA_BUTTON,
    Material.DARK_OAK_BUTTON,
    Material.MANGROVE_BUTTON,
    Material.CRIMSON_BUTTON,
    Material.WARPED_BUTTON,
)

val trapdoors = listOf(
    Material.OAK_TRAPDOOR,
    Material.SPRUCE_TRAPDOOR,
    Material.BIRCH_TRAPDOOR,
    Material.JUNGLE_TRAPDOOR,
    Material.ACACIA_TRAPDOOR,
    Material.DARK_OAK_TRAPDOOR,
    Material.MANGROVE_TRAPDOOR,
    Material.CRIMSON_TRAPDOOR,
    Material.WARPED_TRAPDOOR,
)

val doors = listOf(
    Material.OAK_DOOR,
    Material.SPRUCE_DOOR,
    Material.BIRCH_DOOR,
    Material.JUNGLE_DOOR,
    Material.ACACIA_DOOR,
    Material.DARK_OAK_DOOR,
    Material.MANGROVE_DOOR,
    Material.CRIMSON_DOOR,
    Material.WARPED_DOOR,
)

val logs = listOf(
    Material.OAK_LOG,
    Material.SPRUCE_LOG,
    Material.BIRCH_LOG,
    Material.JUNGLE_LOG,
    Material.ACACIA_LOG,
    Material.DARK_OAK_LOG,
    Material.MANGROVE_LOG,
    Material.CRIMSON_STEM,
    Material.WARPED_STEM,
)

val wood = listOf(
    Material.OAK_WOOD,
    Material.SPRUCE_WOOD,
    Material.BIRCH_WOOD,
    Material.JUNGLE_WOOD,
    Material.ACACIA_WOOD,
    Material.DARK_OAK_WOOD,
    Material.MANGROVE_WOOD,
    Material.CRIMSON_HYPHAE,
    Material.WARPED_HYPHAE,
)

val strippedLogs = listOf(
    Material.STRIPPED_OAK_LOG,
    Material.STRIPPED_SPRUCE_LOG,
    Material.STRIPPED_BIRCH_LOG,
    Material.STRIPPED_JUNGLE_LOG,
    Material.STRIPPED_ACACIA_LOG,
    Material.STRIPPED_DARK_OAK_LOG,
    Material.STRIPPED_MANGROVE_LOG,
    Material.STRIPPED_CRIMSON_STEM,
    Material.STRIPPED_WARPED_STEM,
)

val strippedWood = listOf(
    Material.STRIPPED_OAK_WOOD,
    Material.STRIPPED_SPRUCE_WOOD,
    Material.STRIPPED_BIRCH_WOOD,
    Material.STRIPPED_JUNGLE_WOOD,
    Material.STRIPPED_ACACIA_WOOD,
    Material.STRIPPED_DARK_OAK_WOOD,
    Material.STRIPPED_MANGROVE_WOOD,
    Material.STRIPPED_CRIMSON_HYPHAE,
    Material.STRIPPED_WARPED_HYPHAE,
)

val mushrooms = listOf(
    Material.BROWN_MUSHROOM,
    Material.RED_MUSHROOM
)

val pottedMushrooms = listOf(
    Material.POTTED_BROWN_MUSHROOM,
    Material.POTTED_RED_MUSHROOM
)

val mushroomBlocks = listOf(
    Material.BROWN_MUSHROOM_BLOCK,
    Material.RED_MUSHROOM_BLOCK,
)

val flowers = listOf(
    Material.DANDELION,
    Material.POPPY,
    Material.BLUE_ORCHID,
    Material.ALLIUM,
    Material.AZURE_BLUET,
    Material.RED_TULIP,
    Material.PINK_TULIP,
    Material.WHITE_TULIP,
    Material.ORANGE_TULIP,
    Material.OXEYE_DAISY,
    Material.CORNFLOWER,
    Material.LILY_OF_THE_VALLEY,
)

val pottedFlowers = listOf(
    Material.POTTED_DANDELION,
    Material.POTTED_POPPY,
    Material.POTTED_BLUE_ORCHID,
    Material.POTTED_ALLIUM,
    Material.POTTED_AZURE_BLUET,
    Material.POTTED_RED_TULIP,
    Material.POTTED_PINK_TULIP,
    Material.POTTED_WHITE_TULIP,
    Material.POTTED_ORANGE_TULIP,
    Material.POTTED_OXEYE_DAISY,
    Material.POTTED_CORNFLOWER,
    Material.POTTED_LILY_OF_THE_VALLEY,
)

val tallFlowers = listOf(
    Material.SUNFLOWER,
    Material.LILAC,
    Material.ROSE_BUSH,
    Material.PEONY
)

val dirt = listOf(
    Material.DIRT,
    Material.ROOTED_DIRT,
    Material.COARSE_DIRT,
)

val grass = listOf(
    Material.DIRT_PATH,
    Material.GRASS_BLOCK,
    Material.PODZOL,
    Material.MYCELIUM,
)

val sand = listOf(
    Material.SAND,
    Material.RED_SAND,
)

val sandstone = listOf(
    Material.SANDSTONE,
    Material.RED_SANDSTONE,
)

val nylium = listOf(
    Material.WARPED_NYLIUM,
    Material.CRIMSON_NYLIUM,
)

fun <T> nextOf(list: List<T>, value: T, reversed: Boolean): T? {
    return if (reversed) {
        when (val index = list.indexOf(value)) {
            -1 -> null
            0 -> list[list.size - 1]
            else -> list[index - 1]
        }
    } else {
        when (val index = list.indexOf(value)) {
            -1 -> null
            list.size - 1 -> list[0]
            else -> list[index + 1]
        }
    }
}

@Serializable
@SerialName("cycle-flora")
data class CycleFlora(val reversed: Boolean = false) : Action {
    override fun perform(ctx: Context): Boolean {
        val block = ctx.block ?: return false

        val newType = when (block.type) {
            in saplings -> nextOf(saplings, block.type, reversed)
            in planks -> nextOf(planks, block.type, reversed)
            in slabs -> nextOf(slabs, block.type, reversed)
            in stairs -> nextOf(stairs, block.type, reversed)
            in fences -> nextOf(fences, block.type, reversed)
            in fenceGates -> nextOf(fenceGates, block.type, reversed)
            in buttons -> nextOf(buttons, block.type, reversed)
            in pressurePlates -> nextOf(pressurePlates, block.type, reversed)
            in trapdoors -> nextOf(trapdoors, block.type, reversed)
            in doors -> nextOf(doors, block.type, reversed)
            in logs -> nextOf(logs, block.type, reversed)
            in wood -> nextOf(wood, block.type, reversed)
            in strippedLogs -> nextOf(strippedLogs, block.type, reversed)
            in strippedWood -> nextOf(strippedWood, block.type, reversed)
            in flowers -> nextOf(flowers, block.type, reversed)
            in mushrooms -> nextOf(mushrooms, block.type, reversed)
            in mushroomBlocks -> nextOf(mushroomBlocks, block.type, reversed)
            in pottedFlowers -> nextOf(pottedFlowers, block.type, reversed)
            in pottedMushrooms -> nextOf(pottedMushrooms, block.type, reversed)
            in tallFlowers -> nextOf(tallFlowers, block.type, reversed)
            in dirt -> nextOf(dirt, block.type, reversed)
            in grass -> nextOf(grass, block.type, reversed)
            in sand -> nextOf(sand, block.type, reversed)
            in sandstone -> nextOf(sandstone, block.type, reversed)
            in nylium -> nextOf(nylium, block.type, reversed)
            else -> return false
        } ?: return false

        val blockData = block.blockData
        block.setType(newType, false)

        block.blockData = block.blockData.apply {
            if (this is Orientable) {
                axis = (blockData as Orientable).axis
            }
            if (this is MultipleFacing) {
                (blockData as MultipleFacing).faces.forEach { setFace(it, true) }
            }
            if (this is Bisected) {
                half = (blockData as Bisected).half
                if (block.type.isPartial) {
                    if (half == Bisected.Half.BOTTOM) {
                        val top = block.getRelative(BlockFace.UP)
                        top.setType(newType, false)
                        top.blockData = (top.blockData as Bisected).apply { half = Bisected.Half.TOP }
                    } else {
                        val bottom = block.getRelative(BlockFace.DOWN)
                        bottom.setType(newType, false)
                        bottom.blockData = (bottom.blockData as Bisected).apply { half = Bisected.Half.BOTTOM }
                    }
                }
            }
            if (this is Door) {
                hinge = (blockData as Door).hinge
            }
            if (this is Openable) {
                isOpen = (blockData as Openable).isOpen
            }
            if (this is Directional) {
                facing = (blockData as Directional).facing
            }
            if (this is Waterlogged) {
                isWaterlogged = (blockData as Waterlogged).isWaterlogged
            }
        }

        block.spawnParticle(Particle.HEART, 8, 0.01)
        block.world.playSound(block.location, block.soundGroup.placeSound, 1.0f, 1.0f)

        return true
    }
}
