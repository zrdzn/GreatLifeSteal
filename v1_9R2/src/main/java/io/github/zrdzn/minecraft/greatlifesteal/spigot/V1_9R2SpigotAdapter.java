package io.github.zrdzn.minecraft.greatlifesteal.spigot;

public class V1_9R2SpigotAdapter implements SpigotAdapter {

    @Override
    public String getVersion() {
        return "v1.9.4";
    }

    @Override
    public DamageableAdapter getDamageableAdapter() {
        return new V1_9R2DamageableAdapter();
    }

    @Override
    public ShapedRecipeAdapter getShapedRecipeAdapter() {
        return new V1_8R3ShapedRecipeAdapter();
    }

    @Override
    public RecipeManagerAdapter getRecipeManagerAdapter() {
        return new V1_9R2RecipeManagerAdapter();
    }

}
