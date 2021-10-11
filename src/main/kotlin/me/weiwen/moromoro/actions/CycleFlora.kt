@file:UseSerializers(BiomeSerializer::class)

package me.weiwen.moromoro.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.extensions.spawnParticle
import me.weiwen.moromoro.serializers.BiomeSerializer
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Bisected
import org.bukkit.block.data.Orientable

val planks = listOf(
    Material.OAK_PLANKS,
    Material.SPRUCE_PLANKS,
    Material.BIRCH_PLANKS,
    Material.JUNGLE_PLANKS,
    Material.ACACIA_PLANKS,
    Material.DARK_OAK_PLANKS,
    Material.CRIMSON_PLANKS,
    Material.WARPED_PLANKS,
)

val logs = listOf(
    Material.OAK_LOG,
    Material.SPRUCE_LOG,
    Material.BIRCH_LOG,
    Material.JUNGLE_LOG,
    Material.ACACIA_LOG,
    Material.DARK_OAK_LOG,
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
            in planks -> nextOf(planks, block.type, reversed)
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
            else -> return false
        } ?: return false

        val blockData = block.blockData
        block.setType(newType, false)

        block.blockData = block.blockData.apply {
            when (this) {
                is Orientable -> axis = (blockData as Orientable).axis
                is Bisected -> {
                    half = (blockData as Bisected).half
                    if (half == Bisected.Half.BOTTOM) {
                        val top = block.getRelative(BlockFace.UP)
                        top.setType(newType, false)
                        top.blockData = (top.blockData as Bisected).apply { half = Bisected.Half.TOP}
                    } else {
                        val bottom = block.getRelative(BlockFace.DOWN)
                        bottom.setType(newType, false)
                        bottom.blockData = (bottom.blockData as Bisected).apply { half = Bisected.Half.BOTTOM}
                    }
                }
            }
        }

        block.spawnParticle(Particle.VILLAGER_HAPPY, 8, 0.01)
        block.world.playSound(block.location, block.soundGroup.placeSound, 1.0f, 1.0f)

        return true
    }
}
