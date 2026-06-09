plugins {
    kotlin("jvm") version "2.3.21"
    kotlin("plugin.serialization") version "2.3.21"
}

group = "me.blade"
version = "1.0"

repositories {
    mavenCentral()
}

var ktorVersion = "2.3.12"

dependencies {
    implementation("io.ktor:ktor-server-netty:${ktorVersion}")
    implementation("io.ktor:ktor-server-html-builder:${ktorVersion}")
    implementation("io.ktor:ktor-server-auth:${ktorVersion}")
    implementation("io.ktor:ktor-server-sessions:${ktorVersion}")

    implementation("org.jetbrains.kotlin-wrappers:kotlin-css:2026.6.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.11.0")

    implementation("org.slf4j:slf4j-simple:2.0.13")
    implementation("org.jdom:jdom2:2.0.6.1")
    implementation("org.reflections:reflections:0.10.2")
}

tasks.register<Jar>("fatJar") {
    description = "Bundles this pizdec"
    archiveClassifier.set("all")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = "me.blade.ktorweb.MainKt"
    }
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter {
            it.name.endsWith("jar")
        }.map { zipTree(it) }
    })
}

kotlin {
    jvmToolchain(21)
}