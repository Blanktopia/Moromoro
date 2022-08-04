package me.weiwen.moromoro.hooks

import com.earth2me.essentials.Essentials
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.items.ItemManager
import me.weiwen.moromoro.items.item
import net.ess3.api.IItemDb
import org.bukkit.inventory.ItemStack

class EssentialsHook(
    private val moromoro: Moromoro,
    private val itemManager: ItemManager,
) : Hook, IItemDb.ItemResolver {
    override val name = "Essentials"

    fun register() {
        val plugin = plugin as? Essentials ?: return
        plugin.itemDb.registerResolver(moromoro, "moromoro", this)
    }

    fun unregister() {
        val plugin = plugin as? Essentials ?: return
        plugin.itemDb.unregisterResolver(moromoro, "moromoro")
    }

    override fun apply(type: String): ItemStack? {
        val renamed = type.replace('_', '-')
        return itemManager.templates[renamed]?.item(renamed)
    }

    override fun getNames(): Collection<String> {
        return itemManager.keys.map { it.replace('-', '_') }
    }

    fun getItemStack(name: String): ItemStack? {
        val split = name.split(":")
        val plugin = plugin as? Essentials ?: return null
        return plugin.itemDb.get(split[0], split.getOrNull(1)?.toInt() ?: 1)
    }

    fun getName(item: ItemStack): String? {
        val plugin = plugin as? Essentials ?: return null
        return plugin.itemDb.name(item)
    }
}
