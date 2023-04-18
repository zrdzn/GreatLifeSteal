plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("java")
}

allprojects {
    group = "io.github.zrdzn.minecraft.greatlifesteal"
    version = "1.8-SNAPSHOT"
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.github.johnrengelman.shadow")

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    repositories {
        mavenCentral()
        mavenLocal()

        maven {
            name = "spigot-repository"
            url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        }

        maven {
            name = "sonatype-repository"
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }

        maven {
            name = "reposilite-repository"
            url = uri("https://maven.reposilite.com/maven-central")
        }

        maven {
            name = "panda-repository"
            url = uri("https://repo.panda-lang.org/releases")
        }
    }
}