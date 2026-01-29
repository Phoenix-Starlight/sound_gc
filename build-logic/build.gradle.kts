plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.minecraftforge.net/")
    maven("https://maven.architectury.dev/")
}

dependencies {
    implementation("dev.architectury.loom:dev.architectury.loom.gradle.plugin:1.13.467")
    implementation("architectury-plugin:architectury-plugin.gradle.plugin:3.4.162")
    implementation("com.gradleup.shadow:com.gradleup.shadow.gradle.plugin:8.3.6")
}
