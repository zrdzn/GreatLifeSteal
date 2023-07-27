package io.github.zrdzn.minecraft.greatlifesteal.spigot;

import org.bukkit.inventory.ItemStack;

public interface NbtService {

    void setBoolean(ItemStack itemStack, String key, boolean value);

    boolean getBoolean(ItemStack itemStack, String key);

    void setDouble(ItemStack itemStack, String key, double value);

    double getDouble(ItemStack itemStack, String key);

}
