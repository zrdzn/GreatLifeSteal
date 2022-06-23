package io.github.zrdzn.minecraft.greatlifesteal.spigot;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class V1_9R2DamageableAdapter implements DamageableAdapter {

    @Override
    public double getMaxHealth(Player player) {
        return player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
    }

    @Override
    public void setMaxHealth(Player player, double value) {
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(value);
    }

}
