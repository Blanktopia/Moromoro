@file:Suppress("SpellCheckingInspection")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    id("maven-publish")
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("io.github.goooler.shadow") version "8.1.7"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()

    maven("https://repo.purpurmc.org/snapshots")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.codemc.org/repository/maven-public") // bStats
    maven("https://repo.minebench.de/") // MineDown
    maven("https://repo.essentialsx.net/releases/") // EssentialsX
    maven("https://jitpack.io") // GriefPrevention
    maven("https://maven.enginehub.org/repo/") // WorldGuard
    maven("https://repo.md-5.net/content/groups/public/") // Lib's Disguises
    maven("https://repo.dmulloy2.net/repository/public/") // ProtocolLib
    maven("https://repo.mineinabyss.com")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("com.charleskorn.kaml:kaml:0.57.0")

    compileOnly("org.purpurmc.purpur", "purpur-api", "1.20.6-R0.1-SNAPSHOT")

    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core:2.8.4")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit:2.8.4") { isTransitive = false }
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.1.0-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")

    implementation("cloud.commandframework", "cloud-paper", "1.7.0")
    implementation("org.bstats", "bstats-bukkit", "1.8")
    implementation("de.themoep", "minedown", "1.7.1-SNAPSHOT")
    implementation("com.github.stefvanschie.inventoryframework", "IF", "0.10.14")
    compileOnly("net.essentialsx", "EssentialsX", "2.20.1")
    compileOnly("LibsDisguises", "LibsDisguises", "10.0.44")
    compileOnly("com.github.TechFortress:GriefPrevention:16.18.2")
    compileOnly(files("vendor/GSit-1.2.1.jar"))
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
