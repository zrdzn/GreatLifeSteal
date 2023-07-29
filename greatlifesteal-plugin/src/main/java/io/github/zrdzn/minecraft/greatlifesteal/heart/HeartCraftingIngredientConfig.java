package io.github.zrdzn.minecraft.greatlifesteal.heart;

import eu.okaeri.configs.OkaeriConfig;
import org.bukkit.Material;

public class HeartCraftingIngredientConfig extends OkaeriConfig {

    private Material type;
    private int amount;

    public HeartCraftingIngredientConfig(Material type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    public Material getType() {
        return this.type;
    }

    public void setType(Material type) {
        this.type = type;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

}
