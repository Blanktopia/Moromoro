package me.weiwen.monogoto.hooks

import com.earth2me.essentials.Essentials
import me.weiwen.monogoto.Monogoto
import net.ess3.api.IItemDb
import org.bukkit.inventory.ItemStack

class EssentialsXHook(private val monogoto: Monogoto) : Hook, IItemDb.ItemResolver {
    override val name = "Essentials"

    fun register() {
        val plugin = plugin as? Essentials ?: return
        plugin.itemDb.registerResolver(monogoto, "monogoto", this)
    }

    fun unregister() {
        val plugin = plugin as? Essentials ?: return
        plugin.itemDb.unregisterResolver(monogoto, "monogoto")
    }

    override fun apply(type: String?): ItemStack? {
        return null
    }

    override fun getNames(): Collection<String> {
        return monogoto.itemManager.names
    }
}
