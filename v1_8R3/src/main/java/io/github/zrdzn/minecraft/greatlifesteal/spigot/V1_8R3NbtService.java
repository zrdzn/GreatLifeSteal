package io.github.zrdzn.minecraft.greatlifesteal.spigot;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

public class V1_8R3NbtService implements NbtService {

    @Override
    public void setBoolean(ItemStack itemStack, String key, boolean value) {
        NBTItem nbtItem = new NBTItem(itemStack, true);
        nbtItem.setBoolean(key, value);
    }

    @Override
    public boolean getBoolean(ItemStack itemStack, String key) {
        NBTItem nbtItem = new NBTItem(itemStack, true);
        return nbtItem.getBoolean(key);
    }

    @Override
    public void setDouble(ItemStack itemStack, String key, double value) {
        NBTItem nbtItem = new NBTItem(itemStack, true);
        nbtItem.setDouble(key, value);
    }

    @Override
    public double getDouble(ItemStack itemStack, String key) {
        NBTItem nbtItem = new NBTItem(itemStack, true);
        return nbtItem.getDouble(key);
    }

}
