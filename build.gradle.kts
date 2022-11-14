@file:Suppress("SpellCheckingInspection")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("maven-publish")
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
    id("com.github.johnrengelman.shadow") version "7.1.0"
    kotlin("plugin.serialization")
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
    implementation(libs.idofront.platform.loader)
    compileOnly(libs.kotlin.stdlib)
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.kotlinx.serialization.kaml)
    implementation(libs.idofront.core)

    compileOnly("org.purpurmc.purpur", "purpur-api", "1.19.2-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper", "paper-api", "1.19-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc", "spigot", "1.19-R0.1-SNAPSHOT")

    compileOnly(libs.minecraft.plugin.vault)
    compileOnly(libs.minecraft.plugin.fawe.core)
    compileOnly(libs.minecraft.plugin.fawe.bukkit) { isTransitive = false }
    compileOnly(libs.minecraft.plugin.worldguard)
    compileOnly(libs.minecraft.plugin.protocollib)

    implementation("cloud.commandframework", "cloud-paper", "1.7.0")
    implementation("org.bstats", "bstats-bukkit", "1.8")
    implementation("de.themoep", "minedown", "1.7.1-SNAPSHOT")
    implementation("com.github.stefvanschie.inventoryframework", "IF", "0.10.3")
    compileOnly("net.ess3", "EssentialsX", "2.18.2")
    compileOnly("LibsDisguises", "LibsDisguises", "10.0.24")
    compileOnly("com.github.TechFortress:GriefPrevention:16.18")
    compileOnly(files("vendor/GSit-1.2.1.jar"))
}

bukkit {
    main = "me.weiwen.moromoro.Moromoro"
    apiVersion = "1.19"
    name = "Moromoro"
    version = project.version.toString()
    description = "Easily build custom items for your Minecraft server"
    author = "Goh Wei Wen <goweiwen@gmail.com>"
    website = "weiwen.me"

    depend = listOf("Essentials", "ProtocolLib")
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
