package io.github.zrdzn.minecraft.greatlifesteal.heart;

import org.bukkit.inventory.ShapedRecipe;

public class HeartItem {

    private final int healthAmount;
    private final ShapedRecipe craftingRecipe;

    public HeartItem(int healthAmount, ShapedRecipe craftingRecipe) {
        this.healthAmount = healthAmount;
        this.craftingRecipe = craftingRecipe;
    }

    public int getHealthAmount() {
        return this.healthAmount;
    }

    public ShapedRecipe getCraftingRecipe() {
        return this.craftingRecipe;
    }

}
