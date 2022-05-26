package io.github.zrdzn.minecraft.greatlifesteal.heart;

import io.github.zrdzn.minecraft.greatlifesteal.config.PluginConfig;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

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
    public void addHealth(PlayerInteractEvent event) {
        ItemStack heartItemStack = this.heartItem.getCraftingRecipe().getResult();
        ItemStack item = event.getItem();
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (heartItemStack.isSimilar(item)) {
                Player player = event.getPlayer();
                Map.Entry<Integer, Integer> healthRange = this.config.getHealthRange();

                double playerNewHealth = this.adapter.getMaxHealth(player) + this.heartItem.getHealthAmount();
                if (playerNewHealth <= healthRange.getValue()) {
                    this.adapter.setMaxHealth(player, playerNewHealth);
                    item.setAmount(item.getAmount() - 1);
                }
                event.setCancelled(true);
            }
        }
    }
}
