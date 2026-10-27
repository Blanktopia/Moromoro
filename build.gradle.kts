@file:Suppress("SpellCheckingInspection")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    kotlin("jvm") version "2.4.21"
    kotlin("plugin.serialization") version "2.4.21"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    id("com.gradleup.shadow") version "9.6.1"
    id("maven-publish")
}

repositories {
    mavenCentral()
    google()

    maven("https://repo.purpurmc.org/snapshots")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.codemc.org/repository/maven-public") // bStats, packetevents
    maven("https://repo.minebench.de/") // MineDown
    maven("https://repo.essentialsx.net/snapshots/") // EssentialsX
    maven("https://jitpack.io") // GriefPrevention, VaultAPI
    maven("https://maven.enginehub.org/repo/") // WorldGuard
    maven("https://mvn.lib.co.nz/public") // Lib's Disguises
    maven("https://repo.mineinabyss.com")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.12.0")
    implementation("com.charleskorn.kaml:kaml:0.104.0")

    compileOnly("org.purpurmc.purpur", "purpur-api", "26.3.build.2654-stable")

    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core:2.12.3")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit:2.12.3") { isTransitive = false }
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.1.0-SNAPSHOT")
    compileOnly("com.github.retrooper:packetevents-spigot:2.14.0")

    implementation("org.incendo", "cloud-paper", "2.0.1")
    implementation("org.bstats", "bstats-bukkit", "3.1.0")
    implementation("com.github.stefvanschie.inventoryframework", "IF", "0.12.2")
    compileOnly("net.essentialsx", "EssentialsX", "2.21.0-SNAPSHOT")
    compileOnly("me.libraryaddict.disguises", "libsdisguises", "26.10.24")
    compileOnly("com.github.TechFortress:GriefPrevention:16.18.2")
    compileOnly("com.github.Gecolay.GSit:core:3.5.1")
}

configurations.all {
    resolutionStrategy {
        capabilitiesResolution.withCapability("org.spigotmc:spigot-api:26.3.build.2654-stable") {
            select("org.purpurmc.purpur:purpur-api:26.3.build.2654-stable")
        }
        force("com.google.guava:guava:33.6.0-jre")
        force("com.google.code.gson:gson:2.14.0")
        force("it.unimi.dsi:fastutil:8.5.18")
    }
}

paper {
    main = "me.weiwen.moromoro.Moromoro"
    bootstrapper = "me.weiwen.moromoro.MoromoroBootstrap"
    apiVersion = "26.3"
    name = "Moromoro"
    version = project.version.toString()
    description = "Easily build custom items for your Minecraft server"
    author = "Goh Wei Wen <goweiwen@gmail.com>"
    website = "weiwen.me"

    serverDependencies {
        register("Essentials") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("packetevents") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("Vault") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }

        register("Blanktopia") {
            required = false
        }
        register("LibsDisguises") {
            required = false
        }
        register("GSit") {
            required = false
        }
        register("WorldGuard") {
            required = false
        }
        register("GriefPrevention") {
            required = false
        }
        register("FastAsyncWorldEdit") {
            required = false
        }
        register("ShulkerPacks") {
            required = false
        }
    }
}

tasks.withType<ShadowJar> {
    fun reloc(pkg: String) = relocate(pkg, "$group.dependency.$pkg")

    reloc("org.bstats")
    reloc("de.themoep.minedown")
    reloc("org.incendo.cloud")
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "me.weiwen.moromoro"
            artifactId = "Moromoro"
            version = "1.2.0-SNAPSHOT"

            from(components["java"])
        }
    }
}