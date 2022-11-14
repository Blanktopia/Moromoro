@file:UseSerializers(BiomeSerializer::class)

package me.weiwen.moromoro.actions.mechanic

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.world.biome.BiomeType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.actions.Action
import me.weiwen.moromoro.actions.Context
import me.weiwen.moromoro.addNavigation
import me.weiwen.moromoro.extensions.canBuildAt
import me.weiwen.moromoro.extensions.playSoundTo
import me.weiwen.moromoro.extensions.spawnParticle
import me.weiwen.moromoro.serializers.BiomeSerializer
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import java.util.*

val selectedBiomes: MutableMap<UUID, String> = mutableMapOf()

@Serializable
@SerialName("biome-wand")
data class BiomeWand(val range: Int = 1) : Action {
    override fun perform(ctx: Context): Boolean {
        val player = ctx.player ?: return false

        val block = player.rayTraceBlocks(5.0, FluidCollisionMode.ALWAYS)?.hitBlock ?: return false

        val biome = selectedBiomes[player.uniqueId]
        if (biome != null) {
            setBiome(player, block.location, BiomeType(biome))
        } else {
            player.sendActionBar("${ChatColor.RED}Select a biome first.")
            player.playSoundTo(Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, SoundCategory.PLAYERS, 1.0f, 1.0f)
        }
        return true
    }

    private fun setBiome(player: Player, location: Location, biome: BiomeType) {
        val x0 = location.blockX
        val y0 = location.blockY
        val z0 = location.blockZ

        val world = location.world
        val adaptedWorld = BukkitAdapter.adapt(location.world)

        WorldEdit.getInstance().newEditSession(adaptedWorld).use { session ->
            for (x in -range..range) {
                for (z in -range..range) {
                    for (y in -range..range) {
                        val other = BlockVector3.at(x0 + x, y0 + y, z0 + z)
                        val loc = BukkitAdapter.adapt(world, other)
                        if (player.canBuildAt(loc)) {
                            session.setBiome(other, biome)
                            loc.block.spawnParticle(Particle.VILLAGER_HAPPY, 2, 0.02)
                        }
                    }
                }
            }
        }

        location.world.playSound(location, Sound.BLOCK_GRASS_PLACE, 1.0f, 1.0f)
    }
}

@Serializable
@SerialName("biome-wand-pick")
object BiomeWandPick : Action {
    private var biomes: List<String> = listOf()

    override fun perform(ctx: Context): Boolean {
        if (biomes.isEmpty()) {
            biomes = BiomeType.REGISTRY.keySet().toList().sortedBy { formatBiomeName(it) }
        }

        val player = ctx.player ?: return false

        openGui(player)

        return true
    }

    private fun openGui(player: Player) {
        val gui = ChestGui(6, "Select a Biome:")

        val pages = PaginatedPane(0, 0, 8, 6).apply {
            populateWithGuiItems(biomes.map { biome ->
                GuiItem(ItemStack(biomeMaterial(biome)).apply {
                    setDisplayName("${ChatColor.YELLOW}${formatBiomeName(biome)}")
                }) {
                    selectedBiomes[player.uniqueId] = biome
                    player.sendActionBar("${ChatColor.GOLD}Biome: ${formatBiomeName(biome)}")
                    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
                    it.isCancelled = true
                }
            })
            setOnClick {
                it.isCancelled = true
            }
        }
        gui.addPane(pages)
        gui.addNavigation(pages)

        gui.show(player)
    }
}

fun biomeMaterial(name: String): Material {
    // Nether
    if (name.contains("basalt")) {
        return Material.BASALT
    }
    if (name.contains("soul_sand")) {
        return Material.SOUL_SAND
    }
    if (name.contains("nether")) {
        return Material.NETHERRACK
    }

    // Snow
    if (name.contains("snow")) {
        return Material.SNOW_BLOCK
    }

    // Water
    if (name.contains("beach") || name.contains("ocean") || name.contains("river")) {
        return Material.WATER_BUCKET
    }
    if (name.contains("ice") || name.contains("frozen")) {
        return Material.ICE
    }

    // Forests
    if (name.contains("bamboo")) {
        return Material.BAMBOO
    }
    if (name.contains("crimson")) {
        return Material.CRIMSON_STEM
    }
    if (name.contains("warped")) {
        return Material.WARPED_STEM
    }
    if (name.contains("birch")) {
        return Material.BIRCH_LOG
    }
    if (name.contains("spruce")) {
        return Material.SPRUCE_LOG
    }
    if (name.contains("dark")) {
        return Material.DARK_OAK_LOG
    }
    if (name.contains("swamp")) {
        return Material.MANGROVE_LOG
    }
    if (name.contains("jungle")) {
        return Material.JUNGLE_LOG
    }
    if (name.contains("forest")) {
        return Material.OAK_LOG
    }

    // Deserts
    if (name.contains("gravel")) {
        return Material.GRAVEL
    }
    if (name.contains("badlands")) {
        return Material.RED_SAND
    }
    if (name.contains("mesa")) {
        return Material.TERRACOTTA
    }
    if (name.contains("desert")) {
        return Material.SAND
    }

    // Caves
    if (name.contains("dripstone")) {
        return Material.POINTED_DRIPSTONE
    }
    if (name.contains("cave") || name.contains("stony")) {
        return Material.STONE
    }
    if (name.contains("end")) {
        return Material.END_STONE
    }

    // Terralith
    if (name.contains("sakura")) {
        return Material.PINK_WOOL
    }
    if (name.contains("lavender")) {
        return Material.MAGENTA_WOOL
    }
    if (name.contains("maple") || name.contains("autumn")) {
        return Material.RED_WOOL
    }
    if (name.contains("shield")) {
        return Material.YELLOW_WOOL
    }

    return Material.GRASS_BLOCK
}

fun formatBiomeName(name: String): String {
    return name
        .replace("minecraft:", "")
        .replace(":", ": ")
        .replace("_", " ")
        .split(" ")
        .joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }
}