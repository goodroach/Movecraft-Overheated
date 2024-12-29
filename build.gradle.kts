plugins {
    `java-library`
    `maven-publish`
    id("io.github.0ffz.github-packages") version "1.2.1"
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
}

repositories {
    gradlePluginPortal()
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") // For Paper API
    maven { githubPackage("apdevteam/movecraft")(this) }
    maven("https://maven.playpro.com") // Additional repository, if required
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT") // Paper API (Snapshot, use stable version if possible)
    compileOnly("net.countercraft:movecraft:+") // Movecraft dependency (latest version)
    api("org.jetbrains:annotations-java5:24.1.0") // Optional for better annotations
}

group = "me.goodroach"
version = "1.0.0"
description = "Movecraft-Overheated"

tasks.jar {
    archiveBaseName.set("Movecraft-Overheated")
    archiveClassifier.set("")
    archiveVersion.set("")
}

tasks.processResources {
    val props = mapOf(
            "version" to project.version.toString(), // Make sure "version" is being passed
            "description" to project.description.toString()
    )
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}


