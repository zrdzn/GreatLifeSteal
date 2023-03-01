package io.github.zrdzn.minecraft.greatlifesteal.heart.listeners;

import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

public class HeartCraftListener implements Listener {

    private final HeartItem heartItem;

    public HeartCraftListener(HeartItem heartItem) {
        this.heartItem = heartItem;
    }

    @EventHandler
    public void craftItem(CraftItemEvent event) {
        CraftingInventory inventory = event.getInventory();

        ItemStack[] matrix = inventory.getMatrix();
        if (matrix.length < 10) {
            return;
        }

        ItemStack heart = this.heartItem.result;

        ItemStack eventResult = inventory.getResult();
        if (eventResult == null) {
            return;
        }

        if (!eventResult.isSimilar(heart)) {
            return;
        }

        event.setCancelled(true);

        for (int slotIndex = 1; slotIndex < 10; slotIndex++) {
            ItemStack slotItem = inventory.getItem(slotIndex);
            if (slotItem == null) {
                continue;
            }

            ItemStack ingredient = this.heartItem.ingredients.get(slotIndex);
            int ingredientAmount = ingredient.getAmount();

            int slotItemAmount = slotItem.getAmount();
            if (slotItemAmount <= 0 || slotItemAmount - ingredientAmount <= 0) {
                inventory.clear(slotIndex);
                continue;
            }

            slotItem.setAmount(slotItemAmount - ingredientAmount);
            inventory.setResult(null);
            event.getWhoClicked().setItemOnCursor(heart);
        }
    }

}
