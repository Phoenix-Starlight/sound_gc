pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://maven.minecraftforge.net/")
    }
}

rootProject.name = "sound_gc"

includeBuild("build-logic")
include("common")
include("fabric")
include("forge")
