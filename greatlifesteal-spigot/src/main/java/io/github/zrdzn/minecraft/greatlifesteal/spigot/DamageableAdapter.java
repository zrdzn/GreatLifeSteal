package io.github.zrdzn.minecraft.greatlifesteal.spigot;

import org.bukkit.entity.Player;

public interface DamageableAdapter {

    double getMaxHealth(Player player);

    void setMaxHealth(Player player, double value);

}
