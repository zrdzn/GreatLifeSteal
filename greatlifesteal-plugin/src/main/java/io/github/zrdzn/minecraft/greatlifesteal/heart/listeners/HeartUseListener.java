package io.github.zrdzn.minecraft.greatlifesteal.heart.listeners;

import ch.jalu.configme.SettingsManager;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.BaseConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.MessagesConfig;
import io.github.zrdzn.minecraft.greatlifesteal.heart.configs.HeartConfig;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartItem;
import io.github.zrdzn.minecraft.greatlifesteal.message.MessageService;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.SpigotAdapter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class HeartUseListener implements Listener {

    private final SettingsManager config;
    private final SpigotAdapter spigotAdapter;
    private final HeartItem heartItem;

    public HeartUseListener(SettingsManager config, SpigotAdapter spigotAdapter, HeartItem heartItem) {
        this.config = config;
        this.spigotAdapter = spigotAdapter;
        this.heartItem = heartItem;
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

        double playerNewHealth = this.spigotAdapter.getDamageableAdapter().getMaxHealth(player) + this.heartItem.healthAmount;
        if (playerNewHealth <= this.config.getProperty(BaseConfig.MAXIMUM_HEALTH) && playerNewHealth <= this.config.getProperty(HeartConfig.MAXIMUM_HEALTH_LIMIT)) {
            this.spigotAdapter.getDamageableAdapter().setMaxHealth(player, playerNewHealth);
            this.spigotAdapter.getPlayerInventoryAdapter().removeItem(player.getInventory(), heartItemStack);
        } else {
            MessageService.send(player, this.config.getProperty(MessagesConfig.MAX_HEALTH_REACHED));
        }

        event.setCancelled(true);
    }

}
