package io.github.zrdzn.minecraft.greatlifesteal.heart;

import dev.piotrulla.craftinglib.Crafting;
import org.bukkit.inventory.ItemStack;

public class HeartItem {

    public static final String HEART_CRAFTING_NAME = "glsHeart";
    public static final String HEART_NBT_KEY = "heart";
    public static final String HEART_HEALTH_CHANGE_NBT_KEY = "heart-health-change";

    private final ItemStack itemStack;
    private final Crafting craftingRecipe;

    public HeartItem(ItemStack itemStack, Crafting craftingRecipe) {
        this.itemStack = itemStack;
        this.craftingRecipe = craftingRecipe;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public Crafting getCraftingRecipe() {
        return this.craftingRecipe;
    }

}
