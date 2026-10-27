package me.weiwen.moromoro.resourcepack

import generateItemModels
import generateItems
import me.weiwen.moromoro.Manager
import me.weiwen.moromoro.Moromoro.Companion.plugin
import me.weiwen.moromoro.items.ItemManager
import me.weiwen.moromoro.managers.BlockManager
import org.bukkit.entity.Player
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ResourcePackManager : Manager {
    fun send(player: Player, force: Boolean = false) {
        val url = plugin.config.resourcePackUrl ?: return
        val hash = plugin.config.resourcePackHash
        if (hash.isNullOrEmpty()) {
            player.setResourcePack(url)
        } else {
            player.setResourcePack(url, hash, force)
        }
    }

    fun generate() {
        generateItemModels(ItemManager.templates)
        generateItems(ItemManager.templates)
        generateMushroomBlocks(BlockManager.blockTemplates.values)
        bundleResourcePack()
    }

    private fun bundleResourcePack() {
        // A file in pack/ replaces an overlay file with the same path, so pack/ goes last.
        val entries = sortedMapOf<String, Path>()
        val overlays = File(plugin.dataFolder, "pack-overlays").listFiles { file, s -> file.isDirectory && !s.startsWith("_") } ?: arrayOf()
        for (overlay in overlays.sortedBy { it.name }) {
            addFiles(entries, overlay.toPath(), overlay.toPath().resolve("assets"))
        }
        val pack = File(plugin.dataFolder, "pack").toPath()
        addFiles(entries, pack, pack)

        val zip = File(plugin.dataFolder, "pack.zip")
        zip.outputStream().use { fos ->
            ZipOutputStream(fos).use { zos ->
                zos.setLevel(9)

                for ((name, path) in entries) {
                    zos.putNextEntry(ZipEntry(name))
                    Files.copy(path, zos)
                    zos.closeEntry()
                }
            }
        }
    }

    private fun addFiles(entries: MutableMap<String, Path>, root: Path, folder: Path) {
        if (!Files.isDirectory(folder)) return
        Files.walk(folder).use { paths ->
            paths.filter { Files.isRegularFile(it) && !it.fileName.toString().startsWith(".") && it.fileName.toString() != "desktop.ini" }
                .forEach { entries[root.relativize(it).joinToString("/")] = it }
        }
    }
}