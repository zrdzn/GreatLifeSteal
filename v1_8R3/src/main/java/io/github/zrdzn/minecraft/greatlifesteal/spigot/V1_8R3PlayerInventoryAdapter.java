package io.github.zrdzn.minecraft.greatlifesteal.spigot;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class V1_8R3PlayerInventoryAdapter implements PlayerInventoryAdapter {

    @Override
    public void removeItem(PlayerInventory inventory, ItemStack itemStack) {
        inventory.removeItem(itemStack);
    }

}
