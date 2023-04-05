@file:UseSerializers(
    ItemStackSerializer::class,
    EnchantmentSerializer::class,
    ColorSerializer::class,
    FormattedStringSerializer::class
)

package me.weiwen.moromoro.shop

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.weiwen.moromoro.addNavigation
import me.weiwen.moromoro.extensions.playSoundAt
import me.weiwen.moromoro.extensions.playSoundTo
import me.weiwen.moromoro.serializers.ColorSerializer
import me.weiwen.moromoro.serializers.EnchantmentSerializer
import me.weiwen.moromoro.serializers.FormattedStringSerializer
import me.weiwen.moromoro.serializers.ItemStackSerializer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@Serializable
data class ShopItem(
    val item: ItemStack,
    val price: ItemStack?,
)

@Serializable
data class ShopTemplate(
    val name: String,
    val categories: Map<String, List<ShopItem>>
) {
    fun show(player: Player) {
        val gui = ChestGui(6, name)

        val miniMessage = MiniMessage.miniMessage()

        val pages = PaginatedPane(0, 0, 8, 6)
        pages.populateWithGuiItems(
            categories.map { (category, shopItems) ->
                val item = (shopItems.getOrNull(0)?.item ?: ItemStack(Material.BLACK_STAINED_GLASS_PANE)).clone().apply {
                    itemMeta = itemMeta.apply {
                        displayName(Component.text(category).decoration(TextDecoration.ITALIC, false))
                        lore(
                            listOf(
                                miniMessage.deserialize("<gold>Items: <white>${shopItems.size} ")
                                    .decoration(TextDecoration.ITALIC, false)
                            )
                        )
                    }
                }
                GuiItem(item) { event ->
                    if (event.isLeftClick) {
                        show(player, category, shopItems)
                    }
                }
            }
        )
        pages.setOnClick { it.isCancelled = true }
        gui.addPane(pages)
        gui.addNavigation(pages, null)
        gui.show(player)
    }

    private fun show(player: Player, category: String, items: List<ShopItem>) {
        val gui = ChestGui(6, category)

        val miniMessage = MiniMessage.miniMessage()

        val pages = PaginatedPane(0, 0, 8, 6)
        pages.populateWithGuiItems(
            items.map { shopItem ->
                val item = shopItem.item.clone().apply {
                    itemMeta = itemMeta.apply {
                        lore(
                            listOf(
                                shopItem.price?.let {
                                    miniMessage.deserialize("<gold>Price: <white>${it.amount} ")
                                        .decoration(TextDecoration.ITALIC, false).append(
                                        it.itemMeta.displayName() ?: Component.translatable(it)
                                    )
                                } ?: miniMessage.deserialize("<gold>FREE").decoration(TextDecoration.ITALIC, false)
                            )
                        )
                    }
                }
                GuiItem(item) { event ->
                    if (event.isLeftClick) {
                        buy(player, shopItem)
                    }
                }
            }
        )
        pages.setOnClick { it.isCancelled = true }
        gui.addPane(pages)
        gui.addNavigation(pages) { show(player) }
        gui.show(player)
    }

    private fun buy(player: Player, shopItem: ShopItem): Boolean {
        val (item, price) = shopItem

        val emptySlot = player.inventory.firstEmpty()
        if (emptySlot == -1) {
            player.playSoundTo(Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, SoundCategory.PLAYERS, 1.0f, 1.0f)
            return false
        }

        if (price != null) {
            if (!player.inventory.containsAtLeast(price.asOne(), price.amount)) {
                player.playSoundTo(Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, SoundCategory.PLAYERS, 1.0f, 1.0f)
                return false
            }
            val didntRemove = player.inventory.removeItem(price)
            if (didntRemove.size != 0) {
                player.playSoundTo(Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, SoundCategory.PLAYERS, 1.0f, 1.0f)
                return false
            }
        }

        player.inventory.setItem(emptySlot, item)
        player.playSoundAt(Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.PLAYERS, 1.0f, 0.81f)

        return true
    }
}