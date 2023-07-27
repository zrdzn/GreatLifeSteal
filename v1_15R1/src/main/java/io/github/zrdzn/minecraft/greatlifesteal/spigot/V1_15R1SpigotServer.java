package io.github.zrdzn.minecraft.greatlifesteal.spigot;

import org.bukkit.plugin.Plugin;

public class V1_15R1SpigotServer implements SpigotServer {

    private final Plugin plugin;

    public V1_15R1SpigotServer(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getVersion() {
        return "v1.15.2";
    }

    @Override
    public DamageableAdapter getDamageableAdapter() {
        return new V1_9R2DamageableAdapter();
    }

    @Override
    public PlayerInventoryAdapter getPlayerInventoryAdapter() {
        return new V1_9R2PlayerInventoryAdapter();
    }

    @Override
    public NbtService getNbtService() {
        return new V1_14R1NbtService(this.plugin);
    }

}
