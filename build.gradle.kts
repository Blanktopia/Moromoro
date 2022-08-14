@file:Suppress("SpellCheckingInspection")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.serialization") version "1.6.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "me.weiwen.moromoro"
version = "1.0.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()

    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://papermc.io/repo/repository/maven-public") }
    maven { url = uri("https://repo.purpurmc.org/snapshots") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }

    maven { url = uri("https://repo.codemc.org/repository/maven-public") } // bStats
    maven { url = uri("https://repo.incendo.org/content/repositories/snapshots") } // Cloud
    maven { url = uri("https://repo.minebench.de/") } // MineDown
    maven { url = uri("https://repo.essentialsx.net/releases/") } // EssentialsX
    maven { url = uri("https://jitpack.io") } // GriefPrevention
    maven { url = uri("https://maven.enginehub.org/repo") } // WorldGuard
    maven { url = uri("https://repo.md-5.net/content/groups/public/") } // Lib's Disguises
    maven { url = uri("https://repo.dmulloy2.net/repository/public/") } // ProtocolLib
}


dependencies {
    implementation(kotlin("stdlib", "1.6.0"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
    implementation("com.charleskorn.kaml:kaml:0.33.0")

    compileOnly("org.purpurmc.purpur", "purpur-api", "1.19-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper", "paper-api", "1.19-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc", "spigot", "1.19-R0.1-SNAPSHOT")

    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7")
    implementation("cloud.commandframework", "cloud-paper", "1.7.0")
    implementation("cloud.commandframework", "cloud-kotlin-extensions", "1.7.0")
    implementation("org.bstats", "bstats-bukkit", "1.8")
    implementation("de.themoep", "minedown", "1.7.1-SNAPSHOT")
    implementation("com.github.stefvanschie.inventoryframework", "IF", "0.10.3")
    compileOnly("net.ess3", "EssentialsX", "2.18.2")
    compileOnly("LibsDisguises", "LibsDisguises", "10.0.24")
    compileOnly("com.comphenix.protocol", "ProtocolLib", "4.7.0-SNAPSHOT")
    compileOnly("com.github.TechFortress:GriefPrevention:16.18")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.7")
    compileOnly("com.sk89q.worldedit:worldedit-core:7.2.0-SNAPSHOT")
    compileOnly(files("vendor/GSit-1.2.1.jar"))
}

bukkit {
    main = "me.weiwen.moromoro.Moromoro"
    apiVersion = "1.13"
    name = "Moromoro"
    version = getVersion().toString()
    description = "Easily build custom items for your Minecraft server"
    apiVersion = "1.16"
    author = "Goh Wei Wen <goweiwen@gmail.com>"
    website = "weiwen.me"

    depend = listOf("Essentials", "ProtocolLib", "WorldEdit")
    softDepend = listOf("Blanktopia", "LibsDisguises", "GSit", "WorldGuard", "GriefPrevention")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
    kotlinOptions.languageVersion = "1.6"
    kotlinOptions.freeCompilerArgs = listOf(
        "-Xopt-in=kotlin.RequiresOptIn",
        "-Xuse-experimental=org.jetbrains.kotlinx.serialization.ExperimentalSerializationApi"
    )
}

tasks.withType<ShadowJar> {
    classifier = null

    relocate("org.bstats", "me.weiwen.moromoro.bstats")
    relocate("de.themoep.minedown", "me.weiwen.moromoro.minedown")
    relocate("cloud.commandframework", "me.weiwen.moromoro.cloud")
    relocate("com.github.stefvanschie.inventoryframework", "me.weiwen.moromoro.inventoryframework")
}
