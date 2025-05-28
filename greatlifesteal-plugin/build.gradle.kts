import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

repositories {
    maven {
        name = "jitpack-repository"
        url = uri("https://jitpack.io/")
    }

    maven {
        name = "placeholderapi-repository"
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
}

dependencies {
    implementation(project(":greatlifesteal-spigot"))
    implementation(project(":v1_8R3"))
    implementation(project(":v1_9R2"))
    implementation(project(":v1_10R1"))
    implementation(project(":v1_11R1"))
    implementation(project(":v1_12R1"))
    implementation(project(":v1_13R2"))
    implementation(project(":v1_14R1"))
    implementation(project(":v1_15R1"))

    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")

    compileOnly("me.clip:placeholderapi:2.11.5")

    val okaeriConfigs = "5.0.5"
    implementation("eu.okaeri:okaeri-configs-yaml-bukkit:$okaeriConfigs")
    implementation("eu.okaeri:okaeri-configs-validator-okaeri:$okaeriConfigs")

    implementation("com.github.Querz:NBT:6.1")
    
    implementation("org.slf4j:slf4j-reload4j:2.0.16")

    implementation("org.slf4j:slf4j-reload4j:2.0.13")
    
    implementation("org.apache.commons:commons-lang3:3.17.0")
    
    implementation("org.bstats:bstats-bukkit:3.1.0")

    implementation("com.zaxxer:HikariCP:4.0.3")

    implementation("com.github.zrdzn:CraftingLib:2.0.1")

    val junit = "5.11.3"
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit")

    testImplementation("org.spigotmc:spigot-api:1.8-R0.1-SNAPSHOT")
}

tasks.withType<Test> {
    useJUnitPlatform()

    testLogging {
        events(TestLogEvent.STARTED, TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED)
        exceptionFormat = TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
        showStandardStreams = true
    }
}

java {
    withSourcesJar()
}

tasks.withType<ProcessResources> {
    expand("version" to version)
}

tasks.withType<ShadowJar> {
    archiveFileName.set("GreatLifeSteal v${project.version}.jar")

    val libsPath = "io.github.zrdzn.minecraft.greatlifesteal.libs"
    relocate("org.bstats", "$libsPath.bstats")
    relocate("de.tr7zw.changeme.nbtapi", "$libsPath.nbtapi")
}
