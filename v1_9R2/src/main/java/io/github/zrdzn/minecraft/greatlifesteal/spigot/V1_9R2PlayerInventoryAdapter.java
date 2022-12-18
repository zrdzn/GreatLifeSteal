package io.github.zrdzn.minecraft.greatlifesteal.spigot;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class V1_9R2PlayerInventoryAdapter implements PlayerInventoryAdapter {

    @Override
    public void removeItem(PlayerInventory inventory, ItemStack itemStack) {
        ItemStack itemInHand = inventory.getItemInOffHand();
        if (itemInHand.isSimilar(itemStack)) {
            itemInHand.setAmount(itemInHand.getAmount() - 1);
            return;
        }

        inventory.removeItem(itemStack);
    }

}
