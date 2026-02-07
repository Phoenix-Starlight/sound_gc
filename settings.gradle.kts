pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://maven.neoforged.net/releases/")
    }
}

rootProject.name = "sound_gc"

includeBuild("build-logic")
include("common")
//include("fabric")
include("forge")
project(":forge").name = "neoforge"
