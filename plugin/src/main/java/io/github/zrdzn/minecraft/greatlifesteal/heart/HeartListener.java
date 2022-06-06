package io.github.zrdzn.minecraft.greatlifesteal.heart;

import io.github.zrdzn.minecraft.greatlifesteal.configs.PluginConfig;
import io.github.zrdzn.minecraft.greatlifesteal.message.MessageService;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import org.bukkit.Material;
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

    private final PluginConfig config;
    private final DamageableAdapter adapter;
    private final HeartItem heartItem;

    public HeartListener(PluginConfig config, DamageableAdapter adapter, HeartItem heartItem) {
        this.config = config;
        this.adapter = adapter;
        this.heartItem = heartItem;
    }

    @EventHandler
    public void prepareItem(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();

        ItemStack[] matrix = inventory.getMatrix();
        if (matrix.length < 10) {
            return;
        }

        ItemStack eventResult = inventory.getResult();
        if (eventResult == null) {
            return;
        }

        if (!eventResult.isSimilar(this.heartItem.getResult())) {
            return;
        }

        ItemStack air = new ItemStack(Material.AIR);

        for (int matrixIndex = 0; matrixIndex < 9; matrixIndex++) {
            ItemStack slotItem = matrix[matrixIndex];
            if (slotItem == null) {
                continue;
            }

            ItemStack ingredient = this.heartItem.getIngredients().get(matrixIndex + 1);
            if (ingredient == null) {
                continue;
            }

            if (slotItem.getType() != ingredient.getType()) {
                inventory.setResult(air);
                break;
            }

            if (slotItem.getAmount() < ingredient.getAmount()) {
                inventory.setResult(air);
                break;
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

        ItemStack result = this.heartItem.getResult();

        ItemStack eventResult = inventory.getResult();
        if (eventResult == null) {
            return;
        }

        if (!eventResult.isSimilar(result)) {
            return;
        }

        event.setCancelled(true);

        ItemStack air = new ItemStack(Material.AIR);

        for (int slotIndex = 1; slotIndex < 10; slotIndex++) {
            ItemStack slotItem = inventory.getItem(slotIndex);
            if (slotItem == null) {
                continue;
            }

            ItemStack ingredient = this.heartItem.getIngredients().get(slotIndex);
            int ingredientAmount = ingredient.getAmount();

            int slotItemAmount = slotItem.getAmount();
            if (slotItemAmount <= 0 || slotItemAmount - ingredientAmount <= 0) {
                inventory.clear(slotIndex);
                continue;
            }

            slotItem.setAmount(slotItemAmount - ingredientAmount);
            inventory.setResult(air);
            event.getWhoClicked().setItemOnCursor(result);
        }
    }

    @EventHandler
    public void addHealth(PlayerInteractEvent event) {
        ItemStack heartItemStack = this.heartItem.getResult();
        ItemStack item = event.getItem();
        Action action = event.getAction();

        if (item == null) {
            return;
        }

        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (!heartItemStack.isSimilar(item)) {
            return;
        }

        Player player = event.getPlayer();

        double playerNewHealth = this.adapter.getMaxHealth(player) + this.heartItem.getHealthAmount();
        if (playerNewHealth <= this.config.baseSettings.maximumHealth) {
            this.adapter.setMaxHealth(player, playerNewHealth);
            player.getInventory().removeItem(heartItemStack);
        } else {
            MessageService.send(player, this.config.messages.maxHealthReached);
        }

        event.setCancelled(true);

    }
}
