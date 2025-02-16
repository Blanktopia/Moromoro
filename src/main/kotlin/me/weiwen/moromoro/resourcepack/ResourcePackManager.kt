package me.weiwen.moromoro.resourcepack

import me.weiwen.moromoro.Manager
import me.weiwen.moromoro.Moromoro.Companion.plugin
import me.weiwen.moromoro.items.ItemManager
import me.weiwen.moromoro.managers.BlockManager
import org.bukkit.entity.Player
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ResourcePackManager : Manager {
    fun send(player: Player) {
        val url = plugin.config.resourcePackUrl ?: return
        val hash = plugin.config.resourcePackHash
        if (hash == null || hash.isEmpty()) {
            player.setResourcePack(url)
        } else {
            player.setResourcePack(url, hash)
        }
    }

    fun generate() {
        generateItems(ItemManager.templates)
        generateMushroomBlocks(BlockManager.blockTemplates.values)
        bundleResourcePack()
    }

    private fun bundleResourcePack() {
        val zip = File(plugin.dataFolder, "pack.zip")
        zip.outputStream().use { fos ->
            ZipOutputStream(fos).use { zos ->
                zos.setLevel(9)

                val folder = File(plugin.dataFolder, "pack").path
                val pp = Paths.get(folder)
                val paths = Files.walk(pp)
                paths.filter { !Files.isDirectory(it) }.forEach {
                    zos.putNextEntry(ZipEntry(pp.relativize(it).toString()))
                    Files.copy(it, zos)
                    zos.closeEntry()
                }
            }
        }
    }
}