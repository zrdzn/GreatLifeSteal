package io.github.zrdzn.minecraft.greatlifesteal;

import io.github.zrdzn.minecraft.greatlifesteal.spigot.SpigotServer;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.V1_10R1SpigotServer;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.V1_11R1SpigotServer;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.V1_12R1SpigotServer;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.V1_13R2SpigotServer;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.V1_14R1SpigotServer;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.V1_15R1SpigotServer;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.V1_8R3SpigotServer;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.V1_9R2SpigotServer;
import org.bukkit.plugin.Plugin;

public class SpigotServerFactory {

    private final Plugin plugin;

    public SpigotServerFactory(Plugin plugin) {
        this.plugin = plugin;
    }

    public SpigotServer createServer() {
        String version = this.plugin.getServer().getClass().getPackage().getName().split("\\.")[3];
        switch (version) {
            case "v1_8_R3":
                return new V1_8R3SpigotServer();
            case "v1_9_R2":
                return new V1_9R2SpigotServer();
            case "v1_10_R1":
                return new V1_10R1SpigotServer();
            case "v1_11_R1":
                return new V1_11R1SpigotServer();
            case "v1_12_R1":
                return new V1_12R1SpigotServer();
            case "v1_13_R2":
                return new V1_13R2SpigotServer();
            case "v1_14_R1":
                return new V1_14R1SpigotServer(this.plugin);
            case "v1_15_R1":
            case "v1_16_R3":
            case "v1_17_R1":
            case "v1_18_R2":
            case "v1_19_R1":
            case "v1_19_R2":
            case "v1_19_R3":
            case "v1_20_R1":
                return new V1_15R1SpigotServer(this.plugin);
            default:
                throw new IllegalArgumentException(
                        "Could not find an adapter for the version: " + version + "\n" +
                                "Check supported versions on the resource page."
                );
        }
    }

}
