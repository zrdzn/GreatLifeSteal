package io.github.zrdzn.minecraft.greatlifesteal.heart;

import ch.jalu.configme.SettingsManager;
import io.github.zrdzn.minecraft.greatlifesteal.config.BaseConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.MessagesConfig;
import io.github.zrdzn.minecraft.greatlifesteal.message.MessageFacade;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.NbtService;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.SpigotServer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class HeartUseListener implements Listener {

    private final SettingsManager config;
    private final SpigotServer spigotServer;
    private final HeartItem heartItem;
    private final NbtService nbtService;

    public HeartUseListener(SettingsManager config, SpigotServer spigotServer, HeartItem heartItem, NbtService nbtService) {
        this.config = config;
        this.spigotServer = spigotServer;
        this.heartItem = heartItem;
        this.nbtService = nbtService;
    }

    @EventHandler
    public void addHealth(PlayerInteractEvent event) {
        ItemStack heartItemStack = this.heartItem.getItemStack();
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

        double healthChange = this.nbtService.getDouble(this.heartItem.getItemStack(), HeartItem.HEART_HEALTH_CHANGE_NBT_KEY);

        double playerNewHealth = this.spigotServer.getDamageableAdapter().getMaxHealth(player) + healthChange;
        if (playerNewHealth <= this.config.getProperty(BaseConfig.MAXIMUM_HEALTH) && playerNewHealth <= this.config.getProperty(HeartConfig.MAXIMUM_HEALTH_LIMIT)) {
            this.spigotServer.getDamageableAdapter().setMaxHealth(player, playerNewHealth);
            this.spigotServer.getPlayerInventoryAdapter().removeItem(player.getInventory(), heartItemStack);
        } else {
            MessageFacade.send(player, this.config.getProperty(MessagesConfig.MAX_HEALTH_REACHED));
        }

        event.setCancelled(true);
    }

}
