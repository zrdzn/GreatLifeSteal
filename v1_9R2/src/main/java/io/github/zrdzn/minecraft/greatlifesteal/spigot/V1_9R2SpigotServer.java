package io.github.zrdzn.minecraft.greatlifesteal.spigot;

public class V1_9R2SpigotServer implements SpigotServer {

    @Override
    public String getVersion() {
        return "v1.9.4";
    }

    @Override
    public DamageableAdapter getDamageableAdapter() {
        return new V1_9R2DamageableAdapter();
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
