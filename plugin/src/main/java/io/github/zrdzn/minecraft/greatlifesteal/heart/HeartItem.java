package io.github.zrdzn.minecraft.greatlifesteal.heart;

import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Map;

public class HeartItem {

    private final int healthAmount;
    private final ItemStack result;
    private final Map<Integer, ItemStack> ingredients;

    public HeartItem(int healthAmount, ItemStack result, Map<Integer, ItemStack> ingredients) {
        this.healthAmount = healthAmount;
        this.result = result;
        this.ingredients = ingredients;
    }

    public int getHealthAmount() {
        return this.healthAmount;
    }

    public ItemStack getResult() {
        return this.result.clone();
    }

    public Map<Integer, ItemStack> getIngredients() {
        return Collections.unmodifiableMap(this.ingredients);
    }

}
