@file:Suppress("SpellCheckingInspection")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.10"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("io.github.goooler.shadow") version "8.1.7"
}

repositories {
    mavenCentral()
    google()

    maven("https://repo.purpurmc.org/snapshots")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.codemc.org/repository/maven-public") // bStats
    maven("https://repo.minebench.de/") // MineDown
    maven("https://repo.essentialsx.net/snapshots/") // EssentialsX
    maven("https://jitpack.io") // GriefPrevention, VaultAPI
    maven("https://maven.enginehub.org/repo/") // WorldGuard
    maven("https://repo.md-5.net/content/groups/public/") // Lib's Disguises
    maven("https://repo.dmulloy2.net/repository/public/") // ProtocolLib
    maven("https://repo.mineinabyss.com")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("com.charleskorn.kaml:kaml:0.72.0")

    compileOnly("org.purpurmc.purpur", "purpur-api", "1.21.4-R0.1-SNAPSHOT")

    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core:2.12.3")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit:2.12.3") { isTransitive = false }
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.1.0-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")

    implementation("cloud.commandframework", "cloud-paper", "1.8.4")
    implementation("org.bstats", "bstats-bukkit", "3.1.0")
    implementation("com.github.stefvanschie.inventoryframework", "IF", "0.10.19")
    compileOnly("net.essentialsx", "EssentialsX", "2.21.0-SNAPSHOT")
    compileOnly("LibsDisguises", "LibsDisguises", "10.0.44")
    compileOnly("com.github.TechFortress:GriefPrevention:16.18.2")
    compileOnly(files("vendor/GSit-1.2.1.jar"))
}

configurations.all {
    resolutionStrategy.capabilitiesResolution.withCapability("database:connection-pool") {
        select("com.zaxxer:HikariCP")
    }
    resolutionStrategy {
        capabilitiesResolution.withCapability("org.spigotmc:spigot-api:1.21.4-R0.1-SNAPSHOT") {
            select("org.purpurmc.purpur:purpur-api:1.21.4-R0.1-SNAPSHOT")
        }
        force("com.google.guava:guava:33.3.1-jre")
        force("com.google.code.gson:gson:2.11.0")
        force("it.unimi.dsi:fastutil:8.5.15")
    }
}

bukkit {
    main = "me.weiwen.moromoro.Moromoro"
    apiVersion = "1.20"
    name = "Moromoro"
    version = project.version.toString()
    description = "Easily build custom items for your Minecraft server"
    author = "Goh Wei Wen <goweiwen@gmail.com>"
    website = "weiwen.me"

    depend = listOf("Essentials", "ProtocolLib", "Vault")
    softDepend = listOf("Blanktopia", "LibsDisguises", "GSit", "WorldGuard", "GriefPrevention", "FastAsyncWorldEdit")
}

tasks.withType<ShadowJar> {
    fun reloc(pkg: String) = relocate(pkg, "$group.dependency.$pkg")

    reloc("org.bstats")
    reloc("de.themoep.minedown")
    reloc("cloud.commandframework")
    reloc("com.github.stefvanschie.inventoryframework")
}

val pluginPath = project.findProperty("plugin_path")

if(pluginPath != null) {
    tasks {
        named<DefaultTask>("build") {
            dependsOn("shadowJar")
            doLast {
                copy {
                    from(findByName("reobfJar") ?: findByName("shadowJar") ?: findByName("jar"))
                    into(pluginPath)
                }
            }
        }
    }
}
