package io.github.zrdzn.minecraft.greatlifesteal.spigot;

public class V1_9SpigotAdapter implements SpigotAdapter {

    @Override
    public DamageableAdapter getDamageableAdapter() {
        return new V1_9DamageableAdapter();
    }

    @Override
    public ShapedRecipeAdapter getShapedRecipeAdapter() {
        return new V1_8ShapedRecipeAdapter();
    }

}
