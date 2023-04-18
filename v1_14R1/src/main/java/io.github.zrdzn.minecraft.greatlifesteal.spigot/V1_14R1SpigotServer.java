package io.github.zrdzn.minecraft.greatlifesteal.spigot;

import org.bukkit.plugin.java.JavaPlugin;

public class V1_14R1SpigotServer implements SpigotServer {

    private final JavaPlugin plugin;

    public V1_14R1SpigotServer(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getVersion() {
        return "v1.14.4";
    }

    @Override
    public DamageableAdapter getDamageableAdapter() {
        return new V1_9R2DamageableAdapter();
    }

    @Override
    public ShapedRecipeAdapter getShapedRecipeAdapter() {
        return new V1_12R1ShapedRecipeAdapter(this.plugin);
    }

    @Override
    public RecipeManagerAdapter getRecipeManagerAdapter() {
        return new V1_14R1RecipeManagerAdapter();
    }

    @Override
    public PlayerInventoryAdapter getPlayerInventoryAdapter() {
        return new V1_9R2PlayerInventoryAdapter();
    }

}