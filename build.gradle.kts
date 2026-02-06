plugins {
    id ("architectury-plugin") version "3.4.162"
    id ("soundgc.convention") apply false
}

val maven_group: String by rootProject
val mod_version: String by rootProject
val minecraft_version: String by rootProject

allprojects {
    apply(plugin="architectury-plugin")
    group = maven_group
    version = "$mod_version+$minecraft_version"
    architectury {
        minecraft = minecraft_version
        compileOnly()
    }
}

val copyLogConfig by tasks.register<Soundgc_convention_gradle.CopyIfChangedOrMissing>("copyLogConfig") {
    srcFile = rootProject.file("log4j-dev.xml")
    outFile = rootProject.layout.projectDirectory.dir("run").file("log4j.xml")
}

subprojects {
    apply(plugin="soundgc.convention")
}
