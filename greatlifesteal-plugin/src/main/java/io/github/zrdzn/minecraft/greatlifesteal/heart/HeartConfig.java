package io.github.zrdzn.minecraft.greatlifesteal.heart;

import java.util.HashMap;
import java.util.Map;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import org.bukkit.Material;

public class HeartConfig extends OkaeriConfig {

    @Comment("If the heart item should be enabled on the server.")
    private boolean enabled = true;

    @Comment("")
    @Comment("The amount of health points that should be given to a player upon item consumption.")
    private double healthAmount = 2.0D;

    @Comment("")
    @Comment("Upper limit for maximum health when healing with the heart item.")
    private double maximumHealthLimit = 40.0D;

    @Comment("")
    @Comment("Type of the item that the heart item should be.")
    private Material type = Material.APPLE;

    @Comment("")
    @Comment("Meta configuration for the heart item.")
    private HeartMetaConfig meta = new HeartMetaConfig();

    @Comment("")
    @Comment("Recipe for the heart item creation. Each letter represents a slot in the crafting table.")
    @Comment("Scheme:")
    @Comment("A B C")
    @Comment("D E F")
    @Comment("G H I")
    private Map<String, HeartCraftingIngredientConfig> crafting = new HashMap<String, HeartCraftingIngredientConfig>() {{
        this.put("A", new HeartCraftingIngredientConfig(Material.REDSTONE, 8));
        this.put("B", new HeartCraftingIngredientConfig(Material.DIAMOND_BLOCK, 1));
        this.put("C", new HeartCraftingIngredientConfig(Material.REDSTONE, 8));
        this.put("D", new HeartCraftingIngredientConfig(Material.DIAMOND_BLOCK, 1));
        this.put("E", new HeartCraftingIngredientConfig(Material.EMERALD, 4));
        this.put("F", new HeartCraftingIngredientConfig(Material.DIAMOND_BLOCK, 1));
        this.put("G", new HeartCraftingIngredientConfig(Material.REDSTONE, 8));
        this.put("H", new HeartCraftingIngredientConfig(Material.DIAMOND_BLOCK, 1));
        this.put("I", new HeartCraftingIngredientConfig(Material.REDSTONE, 8));
    }};

    @Comment("")
    @Comment("Everything related to heart item drop.")
    private HeartDropConfig drop = new HeartDropConfig();

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public double getHealthAmount() {
        return this.healthAmount;
    }

    public void setHealthAmount(double healthAmount) {
        this.healthAmount = healthAmount;
    }

    public double getMaximumHealthLimit() {
        return this.maximumHealthLimit;
    }

    public void setMaximumHealthLimit(double maximumHealthLimit) {
        this.maximumHealthLimit = maximumHealthLimit;
    }

    public Material getType() {
        return this.type;
    }

    public void setType(Material type) {
        this.type = type;
    }

    public HeartMetaConfig getMeta() {
        return this.meta;
    }

    public void setMeta(HeartMetaConfig meta) {
        this.meta = meta;
    }

    public Map<String, HeartCraftingIngredientConfig> getCrafting() {
        return this.crafting;
    }

    public void setCrafting(Map<String, HeartCraftingIngredientConfig> crafting) {
        this.crafting = crafting;
    }

    public HeartDropConfig getDrop() {
        return this.drop;
    }

    public void setDrop(HeartDropConfig drop) {
        this.drop = drop;
    }

}
