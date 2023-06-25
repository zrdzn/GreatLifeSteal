package io.github.zrdzn.minecraft.greatlifesteal.heart;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class HeartItem {

    public static final String HEART_NBT_KEY = "heart";
    public static final String HEART_HEALTH_CHANGE_NBT_KEY = "heart-health-change";

    private final ItemStack itemStack;
    private final ShapedRecipe recipe;

    public HeartItem(ItemStack itemStack, ShapedRecipe recipe) {
        this.itemStack = itemStack;
        this.recipe = recipe;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public ShapedRecipe getRecipe() {
        return this.recipe;
    }

}
