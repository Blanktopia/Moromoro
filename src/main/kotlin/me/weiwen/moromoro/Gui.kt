package me.weiwen.moromoro

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*

val NEXT_PAGE_ICON = playerHeadFromHash("Next Page", "61e1e730c77279c8e2e15d8b271a117e5e2ca93d25c8be3a00cc92a00cc0bb85")
val PREV_PAGE_ICON = playerHeadFromHash("Previous Page", "9cdb8f43656c06c4e8683e2e6341b4479f157f48082fea4aff09b37ca3c6995b")
val PARENT_PAGE_ICON = playerHeadFromHash("Back", "81c96a5c3d13c3199183e1bc7f086f54ca2a6527126303ac8e25d63e16b64ccf")

fun playerHeadFromHash(name: String, url: String) =
    ItemStack(Material.PLAYER_HEAD).apply {
        setHeadHash("", url)
        itemMeta = itemMeta.apply {
            displayName(Component.text(name).decoration(TextDecoration.ITALIC, false))
        }
    }

fun ItemStack.setHeadHash(name: String, hash: String) {
    val url = "http://textures.minecraft.net/texture/$hash"
    setHeadUrl(name, url)
}

fun ItemStack.setHeadUrl(name: String, url: String) {
    val bytes = Base64.getEncoder().encode("{textures:{SKIN:{url:\"$url\"}}}".toByteArray())
    setHeadBase64(name, String(bytes))
}

fun ItemStack.setHeadBase64(name: String, base64: String) {
    val uuid = base64.hashCode()
    @Suppress("DEPRECATION")
    Bukkit.getUnsafe().modifyItemStack(
        this,
        "{SkullOwner:{Name:\"$name\",Id:[I;-1,$uuid,-1,$uuid],Properties:{textures:[{Value:\"$base64\"}]}}}"
    )
}

fun ChestGui.addNavigation(pages: PaginatedPane, parent: (() -> Unit)?) {
    val background = OutlinePane(8, 0, 1, 6).apply {
        addItem(GuiItem(ItemStack(Material.BLACK_STAINED_GLASS_PANE).apply {
            itemMeta = itemMeta.apply {
                displayName(Component.text(""))
            }
        }))
        setRepeat(true)
        priority = Pane.Priority.LOWEST
        setOnClick { it.isCancelled = true }
    }
    addPane(background)

    val buttons = StaticPane(8, 0, 1, 6).apply {
        addItem(GuiItem(PREV_PAGE_ICON) {
            if (pages.page > 0) {
                pages.page = pages.page - 1
                update()
            }
            it.isCancelled = true
        }, 0, 4)
        addItem(GuiItem(NEXT_PAGE_ICON) {
            if (pages.page < pages.pages - 1) {
                pages.page = pages.page + 1
                update()
            }
            it.isCancelled = true
        }, 0, 5)
        if (parent != null) {
            addItem(GuiItem(PARENT_PAGE_ICON) {
                parent()
                it.isCancelled = true
            }, 0, 0)
        }
    }
    addPane(buttons)
}