package io.github.zrdzn.minecraft.greatlifesteal.spigot;

import org.bukkit.plugin.java.JavaPlugin;

public class V1_12SpigotAdapter implements SpigotAdapter {

    private final JavaPlugin plugin;

    public V1_12SpigotAdapter(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getVersion() {
        return "v1.12";
    }

    @Override
    public DamageableAdapter getDamageableAdapter() {
        return new V1_9DamageableAdapter();
    }

    @Override
    public ShapedRecipeAdapter getShapedRecipeAdapter() {
        return new V1_12ShapedRecipeAdapter(this.plugin);
    }

}
