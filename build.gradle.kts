import java.text.SimpleDateFormat
import java.util.*

plugins {
    `java-library`
    kotlin("jvm") version "2.1.20"
    id("com.gradleup.shadow") version "8.3.5"
}

allprojects {
    apply {
        plugin("java-library")
        plugin("kotlin")
        plugin("com.gradleup.shadow")
    }

    group = "nl.chimpgamer"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    dependencies {
        compileOnly(kotlin("stdlib"))
    }

    kotlin {
        jvmToolchain(17)
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
            archiveFileName.set("Simple-Giveaway-${project.name.capitalizeWords()}-v${project.version}.jar")
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
