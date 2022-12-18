package io.github.zrdzn.minecraft.greatlifesteal.spigot;

public class V1_8R3SpigotAdapter implements SpigotAdapter {

    @Override
    public String getVersion() {
        return "v1.8.8";
    }

    @Override
    public DamageableAdapter getDamageableAdapter() {
        return new V1_8R3DamageableAdapter();
    }

    @Override
    public ShapedRecipeAdapter getShapedRecipeAdapter() {
        return new V1_8R3ShapedRecipeAdapter();
    }

    @Override
    public RecipeManagerAdapter getRecipeManagerAdapter() {
        return new V1_8R3RecipeManagerAdapter();
    }

    @Override
    public PlayerInventoryAdapter getPlayerInventoryAdapter() {
        return new V1_8R3PlayerInventoryAdapter();
    }

}
