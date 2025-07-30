repositories {
    maven("https://repo.papermc.io/repository/maven-public/") // PaperMC Repository
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")

    compileOnly("dev.dejvokep:boosted-yaml:1.3.7")

    compileOnly("org.incendo:cloud-core:2.0.0")
    compileOnly("org.incendo:cloud-paper:2.0.0-beta.10")
    compileOnly("org.incendo:cloud-minecraft-extras:2.0.0-beta.10")
    compileOnly("org.incendo:cloud-kotlin-coroutines:2.0.0")

    compileOnly("com.github.shynixn.mccoroutine:mccoroutine-folia-api:2.20.0")
    compileOnly("com.github.shynixn.mccoroutine:mccoroutine-folia-core:2.20.0")
}

tasks {
    shadowJar {
        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }
        }
}