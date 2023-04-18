package io.github.zrdzn.minecraft.greatlifesteal.spigot;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

public class V1_8R3NbtService implements NbtService {

    @Override
    public void applyHeartData(ItemStack heartItem) {
        NBTItem nbtItem = new NBTItem(heartItem, true);
        nbtItem.setBoolean("heart", true);
    }

}
