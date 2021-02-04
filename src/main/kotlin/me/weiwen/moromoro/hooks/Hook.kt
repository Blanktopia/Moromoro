package me.weiwen.moromoro.hooks

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin

interface Hook {
    val name: String

    val plugin: Plugin? get() = Bukkit.getServer().pluginManager.getPlugin(name)
}
