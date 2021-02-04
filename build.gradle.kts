import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
    id("net.minecrell.plugin-yml.bukkit")
    id("com.github.johnrengelman.shadow")
}

group = "me.weiwen.moromoro"
version = "1.0.0"

repositories {
    jcenter()

    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }

    // konf
    maven { url = uri("https://jitpack.io") }

    // bStats
    maven { url = uri("https://repo.codemc.org/repository/maven-public") }

    // MineDown
    maven { url = uri("https://repo.minebench.de/") }

    // EssentialsX
    maven { url = uri("https://repo.essentialsx.net/releases/") }
}


dependencies {
    compileOnly(kotlin("stdlib-jdk8", "1.4.21"))
    compileOnly("org.spigotmc", "spigot-api", "1.16.3-R0.1-SNAPSHOT")

    // konf
    implementation("com.github.uchuhimo.konf", "konf", "master-SNAPSHOT")

    // bStats
    implementation("org.bstats", "bstats-bukkit", "1.8")

    // MineDown
    implementation("de.themoep", "minedown", "1.7.0-SNAPSHOT")

    // EssentialsX
    compileOnly("net.ess3", "EssentialsX", "2.18.2")
}

bukkit {
    main = "me.weiwen.monogoto.Monogoto"
    name = "Monogoto"
    version = "1.0.0"
    description = "Easily build custom items for your Minecraft server"
    apiVersion = "1.16"
    author = "Goh Wei Wen <goweiwen@gmail.com>"
    website = "weiwen.me"

    depend = listOf("Kotlin")
    softDepend = listOf("Essentials")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<ShadowJar> {
    classifier = null
}
