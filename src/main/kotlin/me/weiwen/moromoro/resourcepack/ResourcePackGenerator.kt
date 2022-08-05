package me.weiwen.moromoro.resourcepack

import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.items.ItemManager
import me.weiwen.moromoro.managers.BlockManager
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ResourcePackGenerator(
    private val plugin: Moromoro,
    private val itemManager: ItemManager,
    private val blockManager: BlockManager,
) {
    fun generate() {
        generateItems(itemManager.templates.values)
        generateMushroomBlocks(blockManager.blockTemplates.values)
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
