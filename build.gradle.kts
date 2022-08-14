@file:Suppress("SpellCheckingInspection")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.mineinabyss.conventions.kotlin")
//    id("com.mineinabyss.conventions.nms")
    id("maven-publish")
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
    id("com.github.johnrengelman.shadow") version "7.1.0"
    kotlin("plugin.serialization")
}

repositories {
    mavenCentral()

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
    maven { url = uri("https://repo.mineinabyss.com") }
}


dependencies {
    implementation(libs.idofront.platform.loader)
    compileOnly(libs.kotlin.stdlib)
    compileOnly(libs.kotlin.reflect)
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.kotlinx.serialization.kaml)
    compileOnly(libs.kotlinx.coroutines)
    compileOnly(libs.minecraft.mccoroutine)
    compileOnly(libs.koin.core)
    implementation(libs.idofront.core)
    implementation(libs.idofront.nms)

    compileOnly("org.purpurmc.purpur", "purpur-api", "1.19.2-R0.1-SNAPSHOT")
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
    version = project.version.toString()
    description = "Easily build custom items for your Minecraft server"
    apiVersion = "1.16"
    author = "Goh Wei Wen <goweiwen@gmail.com>"
    website = "weiwen.me"

    depend = listOf("Essentials", "ProtocolLib", "WorldEdit")
    softDepend = listOf("Blanktopia", "LibsDisguises", "GSit", "WorldGuard", "GriefPrevention")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "me.weiwen.moromoro"
            artifactId = "Moromoro"
            version = project.version.toString()

            from(components["java"])
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs = listOf("-opt-in=kotlinx.serialization.ExperimentalSerializationApi")
}

tasks.withType<ShadowJar> {
    fun reloc(pkg: String) = relocate(pkg, "$group.dependency.$pkg")

    reloc("org.bstats")
    reloc("de.themoep.minedown")
    reloc("cloud.commandframework")
    reloc("com.github.stefvanschie.inventoryframework")
}