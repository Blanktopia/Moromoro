package me.weiwen.moromoro.hooks

import com.earth2me.essentials.Essentials
import me.weiwen.moromoro.Moromoro
import me.weiwen.moromoro.managers.item
import net.ess3.api.IItemDb
import org.bukkit.inventory.ItemStack

class EssentialsHook(private val moromoro: Moromoro) : Hook, IItemDb.ItemResolver {
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
        return moromoro.itemManager.templates[renamed]?.item(renamed)
    }

    override fun getNames(): Collection<String> {
        return moromoro.itemManager.keys.map { it.replace('-', '_') }
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
