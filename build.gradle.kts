import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform") version "2.3.21"
    `maven-publish`
    id("org.jetbrains.dokka") version "2.2.0"
}

group = "dev.ajthom"
version = "1.0.22"

repositories {
    mavenCentral()
}

fun MavenPom.pomData() {
    name.set("kollections")
    description.set("Kotlin Multiplatform collection helpers — multimaps, multisets, and tables — inspired by Google Guava.")
    url.set("https://github.com/ajthom90/kollections")
    inceptionYear.set("2021")
    licenses {
        license {
            name.set("MIT License")
            url.set("https://opensource.org/licenses/MIT")
        }
    }
    developers {
        developer {
            id.set("ajthom90")
            name.set("Andrew J. Thom")
            email.set("ajthom90@gmail.com")
        }
    }
    scm {
        url.set("https://github.com/ajthom90/kollections")
        connection.set("scm:git:https://github.com/ajthom90/kollections.git")
        developerConnection.set("scm:git:ssh://git@github.com/ajthom90/kollections.git")
    }
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.named("dokkaGeneratePublicationHtml"))
}

kotlin {
    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    js {
        nodejs()
    }

    androidNativeArm32()
    androidNativeArm64()
    androidNativeX64()
    androidNativeX86()

    macosArm64()

    iosArm64()
    iosX64()
    iosSimulatorArm64()

    watchosArm32()
    watchosArm64()
    watchosDeviceArm64()
    watchosSimulatorArm64()

    tvosArm64()
    tvosSimulatorArm64()

    linuxX64()
    linuxArm64()

    mingwX64()

    sourceSets {
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/ajthom90/kollections")
            credentials {
                username = (findProperty("gpr.user") as String?) ?: System.getenv("GITHUB_ACTOR")
                password = (findProperty("gpr.token") as String?) ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications.withType<MavenPublication> {
        artifact(javadocJar)
        pom { pomData() }
    }
}

// Convenience task to publish only the targets that can be built on a macOS host.
val macPlatformTasks = listOf(
    "macosArm64",
    "iosArm64", "iosX64", "iosSimulatorArm64",
    "watchosArm32", "watchosArm64", "watchosDeviceArm64", "watchosSimulatorArm64",
    "tvosArm64", "tvosSimulatorArm64",
).map { getPublishTaskNameForPlatform(it) }.toTypedArray()

tasks.register("buildAndPublishMac") {
    dependsOn(*macPlatformTasks)
}

fun getPublishTaskNameForPlatform(platform: String): String {
    return "publish${capitalizeFirstLetter(platform)}PublicationToGitHubPackagesRepository"
}

fun capitalizeFirstLetter(str: String): String {
    return str.replaceFirstChar { it.uppercase() }
}
