package io.github.zrdzn.minecraft.greatlifesteal.spigot;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class V1_8R3ShapedRecipeAdapter implements ShapedRecipeAdapter {

    @Override
    public ShapedRecipe createShapedRecipe(ItemStack item) {
        return new ShapedRecipe(item.clone());
    }

}
