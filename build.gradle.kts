@file:Suppress("SpellCheckingInspection")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.serialization") version "1.6.0"
    id("net.minecrell.plugin-yml.bukkit")
    id("com.github.johnrengelman.shadow")
}

group = "me.weiwen.moromoro"
version = "1.0.0"

repositories {
    jcenter()
    mavenCentral()

    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://papermc.io/repo/repository/maven-public") }
    maven { url = uri("https://repo.purpurmc.org/snapshots") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }

    // bStats
    maven { url = uri("https://repo.codemc.org/repository/maven-public") }

    // MineDown
    maven { url = uri("https://repo.minebench.de/") }

    // EssentialsX
    maven { url = uri("https://repo.essentialsx.net/releases/") }

    // GriefPrevention
    // ProtectionLib
    maven { url = uri("https://jitpack.io") }

    // ProtectionLib
    maven { url = uri("https://rayzr.dev/repo/") }

    // WorldGuard
    maven { url = uri("https://maven.enginehub.org/repo") }

    // Lib's Disguises
    maven { url = uri("https://repo.md-5.net/content/groups/public/") }

    // ProtocolLib
    maven { url = uri("https://repo.dmulloy2.net/repository/public/") }

    mavenLocal()
}


dependencies {
    implementation(kotlin("stdlib", "1.6.0"))

    // Deserialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
    implementation("com.charleskorn.kaml:kaml:0.33.0")

    // Purpur
    compileOnly("org.purpurmc.purpur", "purpur-api", "1.19-R0.1-SNAPSHOT")

    // Paper
    compileOnly("io.papermc.paper", "paper-api", "1.19-R0.1-SNAPSHOT")

    // Spigot
    compileOnly("org.spigotmc", "spigot", "1.19-R0.1-SNAPSHOT")

    // Vault
    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7")

    // bStats
    implementation("org.bstats", "bstats-bukkit", "1.8")

    // MineDown
    implementation("de.themoep", "minedown", "1.7.1-SNAPSHOT")

    // InventoryFramework
    implementation("com.github.stefvanschie.inventoryframework", "IF", "0.10.3")

    // EssentialsX
    compileOnly("net.ess3", "EssentialsX", "2.18.2")

    // Libs Disguises
    compileOnly("LibsDisguises", "LibsDisguises", "10.0.24")

    // ProtocolLib
    compileOnly("com.comphenix.protocol", "ProtocolLib", "4.7.0-SNAPSHOT")

    // GriefPrevention
    compileOnly("com.github.TechFortress:GriefPrevention:16.18")

    // WorldGuard
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.7")

    // GSit
    compileOnly(files("vendor/GSit-1.0.7.jar"))
}

bukkit {
    main = "me.weiwen.moromoro.Moromoro"
    apiVersion = "1.13"
    name = "Moromoro"
    version = "1.0.0"
    description = "Easily build custom items for your Minecraft server"
    apiVersion = "1.16"
    author = "Goh Wei Wen <goweiwen@gmail.com>"
    website = "weiwen.me"

    depend = listOf("Essentials", "ProtocolLib")
    softDepend = listOf("Blanktopia", "LibsDisguises", "GSit", "WorldGuard", "GriefPrevention")

    commands {
        register("moromoro") {
            description = "Manages the Moromoro plugin"
            usage = "/<command> reload"
            permission = "moromoro.admin"
        }
        register("pack") {
            description = "Sends the resource pack"
            usage = "/<command>"
            permission = "moromoro.pack"
        }
        register("trinkets") {
            description = "Opens the trinket bag"
            usage = "/<command>"
            permission = "moromoro.trinkets"
        }
    }
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
}
