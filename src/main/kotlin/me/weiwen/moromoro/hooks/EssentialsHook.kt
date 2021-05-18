package me.weiwen.moromoro.hooks

import com.earth2me.essentials.Essentials
import me.weiwen.moromoro.Moromoro
import net.ess3.api.IItemDb
import org.bukkit.inventory.ItemStack

class EssentialsHook(private val moromoro: Moromoro) : Hook, IItemDb.ItemResolver {
    override val name = "Essentials"

    fun register() {
        val plugin = plugin as? Essentials ?: return
        plugin.itemDb.registerResolver(moromoro, "monogoto", this)
    }

    fun unregister() {
        val plugin = plugin as? Essentials ?: return
        plugin.itemDb.unregisterResolver(moromoro, "monogoto")
    }

    override fun apply(type: String?): ItemStack? {
        return moromoro.itemManager.templates.get(type)?.build()
    }

    override fun getNames(): Collection<String> {
        return moromoro.itemManager.keys
    }
}