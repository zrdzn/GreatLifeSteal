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
        SpigotServer spigotServer;
        try {
            String version = this.plugin.getServer().getClass().getPackage().getName().split("\\.")[3];

            switch (version) {
                case "v1_8_R3":
                    spigotServer = new V1_8R3SpigotServer();
                    break;
                case "v1_9_R2":
                    spigotServer = new V1_9R2SpigotServer();
                    break;
                case "v1_10_R1":
                    spigotServer = new V1_10R1SpigotServer();
                    break;
                case "v1_11_R1":
                    spigotServer = new V1_11R1SpigotServer();
                    break;
                case "v1_12_R1":
                    spigotServer = new V1_12R1SpigotServer();
                    break;
                case "v1_13_R2":
                    spigotServer = new V1_13R2SpigotServer();
                    break;
                case "v1_14_R1":
                    spigotServer = new V1_14R1SpigotServer(this.plugin);
                    break;
                default:
                    spigotServer = new V1_15R1SpigotServer(this.plugin);
            }
        } catch (ArrayIndexOutOfBoundsException exception) {
            spigotServer = new V1_15R1SpigotServer(this.plugin);
        }

        return spigotServer;
    }

}
