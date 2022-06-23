package io.github.zrdzn.minecraft.greatlifesteal.spigot;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

public class V1_15R1RecipeManagerAdapter implements RecipeManagerAdapter {

    @Override
    public boolean removeServerShapedRecipe(ShapedRecipe recipe) {
        boolean removed = false;
        for (Recipe foundRecipe : Bukkit.getRecipesFor(recipe.getResult())) {
            if (foundRecipe instanceof Keyed) {
                Bukkit.removeRecipe(((Keyed) recipe).getKey());
                removed = true;
            }
        }
        return removed;
    }

}
