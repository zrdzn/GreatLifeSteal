package io.github.zrdzn.minecraft.greatlifesteal.spigot;

public interface SpigotServer {

    String getVersion();

    DamageableAdapter getDamageableAdapter();

    PlayerInventoryAdapter getPlayerInventoryAdapter();

    NbtService getNbtService();

}
