package io.github.zrdzn.minecraft.greatlifesteal.heart;

import io.github.zrdzn.minecraft.greatlifesteal.config.PluginConfig;
import io.github.zrdzn.minecraft.greatlifesteal.message.MessageService;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map.Entry;

public class HeartListener implements Listener {

    private final PluginConfig config;
    private final DamageableAdapter adapter;
    private final MessageService messageService;
    private final HeartItem heartItem;

    public HeartListener(PluginConfig config, DamageableAdapter adapter, MessageService messageService, HeartItem heartItem) {
        this.config = config;
        this.adapter = adapter;
        this.messageService = messageService;
        this.heartItem = heartItem;
    }

    @EventHandler
    public void addHealth(PlayerInteractEvent event) {
        ItemStack heartItemStack = this.heartItem.getCraftingRecipe().getResult();
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
        Entry<Integer, Integer> healthRange = this.config.getHealthRange();

        double playerNewHealth = this.adapter.getMaxHealth(player) + this.heartItem.getHealthAmount();
        if (playerNewHealth <= healthRange.getValue()) {
            this.adapter.setMaxHealth(player, playerNewHealth);
            item.setAmount(item.getAmount() - 1);
        } else {
            this.messageService.send(player, "maxHealthReached");
        }

        event.setCancelled(true);

    }
}
