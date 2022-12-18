package io.github.zrdzn.minecraft.greatlifesteal.spigot;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public interface PlayerInventoryAdapter {

    void removeItem(PlayerInventory inventory, ItemStack itemStack);

}
