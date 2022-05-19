package io.github.zrdzn.minecraft.greatlifesteal.spigot;

import org.bukkit.entity.Player;

public class V1_8DamageableAdapter implements DamageableAdapter {

    @Override
    public double getMaxHealth(Player player) {
        return player.getMaxHealth();
    }

    @Override
    public void setMaxHealth(Player player, double value) {
        player.setMaxHealth(value);
    }

}
