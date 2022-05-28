package io.github.zrdzn.minecraft.greatlifesteal.spigot;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public interface ShapedRecipeAdapter {

    ShapedRecipe createRecipe(ItemStack item);

}
