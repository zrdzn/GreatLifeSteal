package io.github.zrdzn.minecraft.greatlifesteal.heart;

import ch.jalu.configme.SettingsManager;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.BaseConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.MessagesConfig;
import io.github.zrdzn.minecraft.greatlifesteal.message.MessageService;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

public class HeartListener implements Listener {

    private final SettingsManager config;
    private final DamageableAdapter adapter;
    private final HeartItem heartItem;

    public HeartListener(SettingsManager config, DamageableAdapter adapter, HeartItem heartItem) {
        this.config = config;
        this.adapter = adapter;
        this.heartItem = heartItem;
    }

    @EventHandler
    public void prepareItem(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();

        ItemStack eventResult = inventory.getResult();
        if (eventResult == null) {
            return;
        }

        if (!eventResult.isSimilar(this.heartItem.result)) {
            return;
        }

        Map<Integer, ItemStack> ingredients = this.heartItem.ingredients;

        for (int matrixIndex = 0; matrixIndex < 9; matrixIndex++) {
            ItemStack slotItem = inventory.getMatrix()[matrixIndex];
            if (slotItem == null) {
                continue;
            }

            ItemStack ingredient = ingredients.get(matrixIndex + 1);

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

    @EventHandler
    public void addHealth(PlayerInteractEvent event) {
        ItemStack heartItemStack = this.heartItem.result;
        if (heartItemStack == null) {
            return;
        }

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        if (!heartItemStack.isSimilar(item)) {
            return;
        }

        Player player = event.getPlayer();

        double playerNewHealth = this.adapter.getMaxHealth(player) + this.heartItem.healthAmount;
        if (playerNewHealth <= this.config.getProperty(BaseConfig.MAXIMUM_HEALTH)) {
            this.adapter.setMaxHealth(player, playerNewHealth);
            player.getInventory().removeItem(heartItemStack);
        } else {
            MessageService.send(player, this.config.getProperty(MessagesConfig.MAX_HEALTH_REACHED));
        }

        event.setCancelled(true);

    }

}
