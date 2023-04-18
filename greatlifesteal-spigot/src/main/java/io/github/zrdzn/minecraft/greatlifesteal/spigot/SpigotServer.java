package io.github.zrdzn.minecraft.greatlifesteal.spigot;

public interface SpigotServer {

    String getVersion();

    DamageableAdapter getDamageableAdapter();

    ShapedRecipeAdapter getShapedRecipeAdapter();

    RecipeManagerAdapter getRecipeManagerAdapter();

    PlayerInventoryAdapter getPlayerInventoryAdapter();

}
