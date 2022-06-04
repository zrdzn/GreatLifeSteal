package io.github.zrdzn.minecraft.greatlifesteal.spigot;

public class V1_8SpigotAdapter implements SpigotAdapter {

    @Override
    public String getVersion() {
        return "v1.8";
    }

    @Override
    public DamageableAdapter getDamageableAdapter() {
        return new V1_8DamageableAdapter();
    }

    @Override
    public ShapedRecipeAdapter getShapedRecipeAdapter() {
        return new V1_8ShapedRecipeAdapter();
    }

}
