import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toUpperCaseAsciiOnly

plugins {
    kotlin("multiplatform") version "1.6.10"
    `maven-publish`
    id("net.linguica.maven-settings") version "0.5"
    id("org.jetbrains.dokka") version "1.6.0"
}

group = "dev.ajthom"
version = "1.0.20"

repositories {
    mavenCentral()
}

val dokkaOutputDir = "$buildDir/dokka"

//tasks.getByName<DokkaTask>("dokkaHtml") {
//    outputDirectory.set(file(dokkaOutputDir))
//}

fun MavenPom.pomData() {
    inceptionYear.set("2021")
    developers {
        developer {
            email.set("ajthom90@gmail.com")
            name.set("Andrew J. Thom")
        }
    }
}

tasks.getByName<DokkaTask>("dokkaHtml") {
    outputDirectory.set(file(dokkaOutputDir))
}

val deleteDokkaOutputDirectory by tasks.register<Delete>("deleteDokkaOutputDirectory") {
    delete(dokkaOutputDir)
}

val javadocJar = tasks.register<Jar>("javadocJar") {
    dependsOn(deleteDokkaOutputDirectory, tasks.dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaOutputDir)
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    androidNativeArm32()
    androidNativeArm64()
    androidNativeX64()
    androidNativeX86()
    macosX64()
    macosArm64()
    ios()
    watchos()
    tvos()
    linuxX64()
    linuxArm64()
    linuxArm32Hfp()
    linuxMips32()
    linuxMipsel32()
    mingwX64()
    mingwX86()

    publishing {
        repositories {
            maven {
                url = uri("https://maven.pkg.github.com/ajthom90/kollections")
                name = "GitHubPackages"
                authentication {
                    create<BasicAuthentication>("basic")
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

val macPlatformTasks = arrayOf("macosX64", "macosArm64", "iosX64", "iosArm64", "watchosX64", "watchosArm64", "watchosArm32", "tvosX64", "tvosArm64").map { getPublishTaskNameForPlatform(it) }.toTypedArray()

tasks.register("buildAndPublishMac") {
    dependsOn(*macPlatformTasks)
}

fun getPublishTaskNameForPlatform(platform: String): String {
    return "publish${capitalizeFirstLetter(platform)}PublicationToGitHubPackagesRepository"
}

fun capitalizeFirstLetter(str: String): String {
    return str.substring(0, 1).toUpperCaseAsciiOnly() + str.substring(1)
}

//tasks.getByName()
