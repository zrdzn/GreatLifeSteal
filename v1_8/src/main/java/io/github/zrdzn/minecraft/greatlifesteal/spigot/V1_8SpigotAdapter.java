package io.github.zrdzn.minecraft.greatlifesteal.spigot;

public class V1_8SpigotAdapter implements SpigotAdapter {

    @Override
    public DamageableAdapter getDamageableAdapter() {
        return new V1_8DamageableAdapter();
    }

}