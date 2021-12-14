import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    kotlin("multiplatform") version "1.6.10"
    `maven-publish`
    id("net.linguica.maven-settings") version "0.5"
    id("org.jetbrains.dokka") version "1.6.0"
}

group = "dev.ajthom"
version = "1.0.19"

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
    iosArm32()
    iosArm64()
    iosX64()
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
