package me.weiwen.moromoro

import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap
import io.papermc.paper.plugin.bootstrap.PluginProviderContext
import me.weiwen.moromoro.items.EnchantmentManager
import org.bukkit.plugin.java.JavaPlugin

class MoromoroBootstrap : PluginBootstrap {

    lateinit var config: MoromoroConfig

    override fun bootstrap(context: BootstrapContext) {
        val logger = context.logger
        val dataFolder = context.dataDirectory.toFile()

        config = parseConfig(logger, dataFolder)

        EnchantmentManager.bootstrap(context, config)
    }

    override fun createPlugin(context: PluginProviderContext): JavaPlugin {
        return Moromoro(config)
    }
}