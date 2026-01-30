plugins {
    id ("dev.architectury.loom") version "1.13.467" apply false
    id ("architectury-plugin") version "3.4.162"
    id ("com.gradleup.shadow") version "8.3.6"
    id ("soundgc.convention") apply false
}

val maven_group: String by rootProject
val mod_version: String by rootProject
val minecraft_version: String by rootProject

allprojects {
    apply(plugin="architectury-plugin")
    group = maven_group
    version = mod_version
    architectury {
        minecraft = minecraft_version
        compileOnly()
    }
}

subprojects {
    apply(plugin="soundgc.convention")
}
