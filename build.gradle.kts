import org.jetbrains.kotlin.util.capitalizeDecapitalize.toUpperCaseAsciiOnly

plugins {
    kotlin("multiplatform") version "1.6.10"
    `maven-publish`
    id("org.jetbrains.dokka") version "1.6.0"
}

group = "dev.ajthom"
version = "1.0.22"

repositories {
    mavenCentral()
}

val dokkaOutputDir = "$buildDir/dokka"

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

tasks.dokkaHtml {
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

    js {
        nodejs()
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
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/ajthom90/kollections")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
        publications.withType<MavenPublication> {
            artifact(javadocJar)
            pom { pomData() }
        }
    }

    sourceSets {
        val commonMain by getting
        getByName("commonTest") {
            dependencies {
                dependsOn(commonMain)
                implementation(kotlin("test"))
            }
        }
    }
}

val macPlatformTasks = listOf("macosX64", "macosArm64", "iosX64", "iosArm64", "watchosX64", "watchosArm64", "watchosArm32", "tvosX64", "tvosArm64").map { getPublishTaskNameForPlatform(it) }.toTypedArray()

tasks.register("buildAndPublishMac") {
    dependsOn(*macPlatformTasks)
}

fun getPublishTaskNameForPlatform(platform: String): String {
    return "publish${capitalizeFirstLetter(platform)}PublicationToGitHubPackagesRepository"
}

fun capitalizeFirstLetter(str: String): String {
    return str.substring(0, 1).toUpperCaseAsciiOnly() + str.substring(1)
}
