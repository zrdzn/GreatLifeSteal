package io.github.zrdzn.minecraft.greatlifesteal.spigot;

import org.bukkit.plugin.java.JavaPlugin;

public class V1_13R2SpigotAdapter implements SpigotAdapter {

    private final JavaPlugin plugin;

    public V1_13R2SpigotAdapter(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getVersion() {
        return "v1.13.2";
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
        return new V1_13R2RecipeManagerAdapter();
    }

}