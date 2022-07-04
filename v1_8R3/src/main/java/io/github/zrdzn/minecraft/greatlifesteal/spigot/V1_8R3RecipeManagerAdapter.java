package io.github.zrdzn.minecraft.greatlifesteal.spigot;

import java.util.Arrays;
import net.minecraft.server.v1_8_R3.CraftingManager;
import org.bukkit.inventory.ShapedRecipe;

public class V1_8R3RecipeManagerAdapter implements RecipeManagerAdapter {

    @Override
    public boolean removeServerShapedRecipe(ShapedRecipe recipe) {
        return CraftingManager.getInstance().getRecipes().removeIf(iRecipe -> {
            if (!(iRecipe instanceof ShapedRecipe)) {
                return false;
            }

            ShapedRecipe foundRecipe = (ShapedRecipe) iRecipe;
            return foundRecipe.getResult().isSimilar(recipe.getResult()) &&
                    Arrays.equals(foundRecipe.getShape(), recipe.getShape());
        });
    }

}