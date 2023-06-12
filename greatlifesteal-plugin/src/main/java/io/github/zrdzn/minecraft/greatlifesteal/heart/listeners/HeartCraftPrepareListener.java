package io.github.zrdzn.minecraft.greatlifesteal.heart.listeners;

import java.util.Map;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

public class HeartCraftPrepareListener implements Listener {

    private final HeartItem heartItem;

    public HeartCraftPrepareListener(HeartItem heartItem) {
        this.heartItem = heartItem;
    }

    @EventHandler
    public void prepareItem(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();

        ItemStack eventResult = inventory.getResult();
        if (eventResult == null) {
            return;
        }

        if (!eventResult.isSimilar(this.heartItem.getRecipe().getResult())) {
            return;
        }

        Map<Character, ItemStack> ingredients = this.heartItem.getRecipe().getIngredientMap();

        for (int matrixIndex = 0; matrixIndex < 9; matrixIndex++) {
            ItemStack slotItem = inventory.getMatrix()[matrixIndex];
            if (slotItem == null) {
                continue;
            }

            ItemStack ingredient = ingredients.get((char) ((matrixIndex + 1) + '0'));

            if (matrixIndex + 1 <= ingredients.size()) {
                if (ingredient == null) {
                    continue;
                }
            }

            if (ingredient != null) {
                if (slotItem.getType() != ingredient.getType()) {
                    inventory.setResult(null);
                    break;
                }

                if (slotItem.getAmount() < ingredient.getAmount()) {
                    inventory.setResult(null);
                    break;
                }
            }
        }
    }

}
