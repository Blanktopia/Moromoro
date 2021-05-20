package me.weiwen.moromoro.hooks

import com.earth2me.essentials.Essentials
import me.weiwen.moromoro.Moromoro
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

    override fun apply(type: String?): ItemStack? {
        return moromoro.itemManager.templates.get(type)?.build()
    }

    override fun getNames(): Collection<String> {
        return moromoro.itemManager.keys
    }

    fun getItemStack(name: String): ItemStack? {
        val plugin = plugin as? Essentials ?: return null
        return plugin.itemDb.get(name)
    }

    fun getName(item: ItemStack): String? {
        val plugin = plugin as? Essentials ?: return null
        return plugin.itemDb.name(item)
    }
}
