package io.github.zrdzn.minecraft.greatlifesteal.spigot;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public class V1_15R1SpigotServer implements SpigotServer {

    private final Plugin plugin;
    private final NamespacedKey namespacedKey;

    public V1_15R1SpigotServer(Plugin plugin) {
        this.plugin = plugin;
        this.namespacedKey = new NamespacedKey(plugin, "heart");
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
    public ShapedRecipeAdapter getShapedRecipeAdapter() {
        return new V1_12R1ShapedRecipeAdapter(this.plugin);
    }

    @Override
    public RecipeManagerAdapter getRecipeManagerAdapter() {
        return new V1_15R1RecipeManagerAdapter();
    }

    @Override
    public PlayerInventoryAdapter getPlayerInventoryAdapter() {
        return new V1_9R2PlayerInventoryAdapter();
    }

    @Override
    public NbtService getNbtService() {
        return new V1_14R1NbtService(this.namespacedKey);
    }

}
