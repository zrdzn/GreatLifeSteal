dependencies {
    compileOnly(project(":greatlifesteal-spigot"))
    compileOnly(project(":v1_8R3"))
    compileOnly(project(":v1_9R2"))
    compileOnly(project(":v1_12R1"))

    compileOnly("org.spigotmc:spigot:1.13.2-R0.1-SNAPSHOT")

    // Required to work with CraftBukkit's relocated library
    compileOnly("it.unimi.dsi:fastutil:8.5.12")

    implementation("de.tr7zw:item-nbt-api:2.12.0")
}
