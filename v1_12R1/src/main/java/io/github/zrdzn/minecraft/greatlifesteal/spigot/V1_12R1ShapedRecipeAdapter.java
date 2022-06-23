package io.github.zrdzn.minecraft.greatlifesteal.spigot;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class V1_12R1ShapedRecipeAdapter implements ShapedRecipeAdapter {

    private final JavaPlugin plugin;

    public V1_12R1ShapedRecipeAdapter(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ShapedRecipe createShapedRecipe(ItemStack item) {
        return new ShapedRecipe(new NamespacedKey(this.plugin, "heartitemrecipe"), item.clone());
    }

}
