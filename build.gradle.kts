import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform") version "2.3.21"
    id("org.jetbrains.dokka") version "2.2.0"
    id("com.vanniktech.maven.publish") version "0.36.0"
}

group = "io.github.ajthom90"
version = "1.0.22"

repositories {
    mavenCentral()
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

mavenPublishing {
    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationHtml"),
            sourcesJar = true,
        )
    )

    publishToMavenCentral()
    signAllPublications()

    coordinates("io.github.ajthom90", "kollections", "1.0.22")

    pom {
        name.set("kollections")
        description.set("Kotlin Multiplatform collection helpers — multimaps, multisets, and tables — inspired by Google Guava.")
        url.set("https://github.com/ajthom90/kollections")
        inceptionYear.set("2021")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set("ajthom90")
                name.set("Andrew J. Thom")
                url.set("https://github.com/ajthom90")
            }
        }
        scm {
            url.set("https://github.com/ajthom90/kollections")
            connection.set("scm:git:https://github.com/ajthom90/kollections.git")
            developerConnection.set("scm:git:ssh://git@github.com/ajthom90/kollections.git")
        }
    }
}
