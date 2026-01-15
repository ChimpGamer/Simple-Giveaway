import java.text.SimpleDateFormat
import java.util.*

plugins {
    `java-library`
    kotlin("jvm") version "2.3.0"
    id("com.gradleup.shadow") version "9.3.1"
}

allprojects {
    apply {
        plugin("java-library")
        plugin("kotlin")
        plugin("com.gradleup.shadow")
    }

    group = "nl.chimpgamer"
    version = "1.0.1"

    repositories {
        mavenCentral()
    }

    dependencies {
        compileOnly(kotlin("stdlib"))
    }

    kotlin {
        jvmToolchain(21)
    }
}

subprojects {

    tasks {
        processResources {
            filesMatching("**/*.yml") {
                val buildNumber = System.getenv("BUILD_NUMBER") ?: "SNAPSHOT"
                expand("version" to project.version, "buildDate" to getDate(), "buildNumber" to buildNumber)
            }
        }

        shadowJar {
            val buildNumber = System.getenv("BUILD_NUMBER")
            val name = if (buildNumber == null) {
                "Simple-Giveaway-${project.name.capitalizeWords()}-v${project.version}.jar"
            } else {
                "Simple-Giveaway-${project.name.capitalizeWords()}-v${project.version}-b$buildNumber.jar"
            }
            archiveFileName.set(name)
        }

        build {
            dependsOn(shadowJar)
        }
    }
}

fun getDate(): String {
    val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
    val date = Date()
    return simpleDateFormat.format(date)
}

/**
 * Will capitalize the first letter of every word
 */
fun String.capitalizeWords() = split("[ _]".toRegex()).joinToString(" ") { s -> s.lowercase()
    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } }
