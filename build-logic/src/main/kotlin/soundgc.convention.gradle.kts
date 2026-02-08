import org.gradle.kotlin.dsl.accessors.runtime.maybeRegister

plugins {
    base
    `java-library`
    id("dev.architectury.loom")
    id("com.gradleup.shadow")
    `maven-publish`
}

val mod_id: String by rootProject
val minecraft_version: String by rootProject

base {
    // Set up a suffixed format for the mod jar names, e.g. `example-fabric`.
    archivesName = "$mod_id-${project.name}"
}

// Files in this configuration will be bundled into your mod using the Shadow plugin.
// Don't use the `shadow` configuration from the plugin itself as it"s meant for excluding files.
val shadowBundle by configurations.registering {
    isCanBeResolved = true
    isCanBeConsumed = false
}

tasks.shadowJar {
    configurations = listOf(shadowBundle.get())
    archiveClassifier = "shadow"
}

if (project.path != ":common") {
    loom {
        mods {
            maybeRegister(this, "main") {
                sourceSet(project.sourceSets.main.get())
                sourceSet(project(":common").sourceSets.main.get())
            }
        }
    }
}

repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.
    maven("https://nexus.gtnewhorizons.com/repository/public/") {
        mavenContent {
            includeGroup("com.github.GTNewHorizons")
        }
    }
}

val copyLogConfig by rootProject.tasks.named<CopyIfChangedOrMissing>("copyLogConfig")

loom {
    silentMojangMappingsLicense()
    log4jConfigs.from(copyLogConfig.outFile)
}

tasks.runClient.configure {
    javaLauncher = javaToolchains.launcherFor {
        languageVersion = JavaLanguageVersion.of(17)
    }
    dependsOn(copyLogConfig)
}

dependencies {
    minecraft("net.minecraft:minecraft:$minecraft_version")
    mappings(loom.officialMojangMappings())
    annotationProcessor("com.github.GTNewHorizons:jabel-javac-plugin:1.0.2-GTNH")
    compileOnly("com.github.GTNewHorizons:jabel-javac-plugin:1.0.2-GTNH")
}

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = "21" // for the IDE support
    options.release = 8

    options.compilerArgs.add("-Xlint:-options")
    options.forkOptions.jvmArgs = listOf("-XX:+EnableDynamicAgentLoading")

    javaCompiler = javaToolchains.compilerFor {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.processResources {
    filesMatching(listOf("**/*.toml", "*.json")) {
        expand(properties + properties.filter { it.key.contains("placeholder.") }.mapKeys { it.key.removePrefix("placeholder.")})
    }
}

// Configure Maven publishing.
publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            artifactId = base.archivesName.get()
            from(components["java"])
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
        // Notice: This block does NOT have the same function as the block in the top level.
        // The repositories here will be used for publishing your artifact, not for
        // retrieving dependencies.
    }
}
