package io.github.zrdzn.minecraft.greatlifesteal.spigot;

public interface SpigotAdapter {

    String getVersion();

    DamageableAdapter getDamageableAdapter();

    ShapedRecipeAdapter getShapedRecipeAdapter();

    RecipeManagerAdapter getRecipeManagerAdapter();

    PlayerInventoryAdapter getPlayerInventoryAdapter();

}
