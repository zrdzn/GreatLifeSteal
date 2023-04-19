package io.github.zrdzn.minecraft.greatlifesteal.spigot;

public class V1_11R1SpigotServer implements SpigotServer {

    @Override
    public String getVersion() {
        return "v1.11.2";
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
        return new V1_11R1RecipeManagerAdapter();
    }

    @Override
    public PlayerInventoryAdapter getPlayerInventoryAdapter() {
        return new V1_9R2PlayerInventoryAdapter();
    }

    @Override
    public NbtService getNbtService() {
        return new V1_8R3NbtService();
    }

}
