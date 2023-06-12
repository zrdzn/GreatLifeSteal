package io.github.zrdzn.minecraft.greatlifesteal.heart;

import java.util.Map;
import ch.jalu.configme.SettingsManager;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.MessagesConfig;
import io.github.zrdzn.minecraft.greatlifesteal.heart.configs.HeartDropConfig;
import io.github.zrdzn.minecraft.greatlifesteal.message.MessageService;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.PlayerInventoryAdapter;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class HeartService {

    private final SettingsManager config;
    private final HeartItem heartItem;
    private final PlayerInventoryAdapter playerInventoryAdapter;

    public HeartService(SettingsManager config, HeartItem heartItem, PlayerInventoryAdapter playerInventoryAdapter) {
        this.config = config;
        this.heartItem = heartItem;
        this.playerInventoryAdapter = playerInventoryAdapter;
    }

    public void giveHeartToPlayer(Player player) {
        HeartDropLocation location = this.config.getProperty(HeartDropConfig.LOCATION);
        if (this.dropHeart(player, location)) {
            return;
        }

        PlayerInventory inventory = player.getInventory();

        Map<Integer, ItemStack> remainingItems = inventory.addItem(this.heartItem.getItemStack());
        if (remainingItems.isEmpty()) {
            return;
        }

        HeartDropLocation fullInventoryLocation = this.config.getProperty(HeartDropConfig.FULL_INVENTORY_LOCATION);
        if (this.dropHeart(player, fullInventoryLocation)) {
            return;
        }

        this.playerInventoryAdapter.removeItem(inventory, this.heartItem.getItemStack());
        MessageService.send(player, this.config.getProperty(MessagesConfig.NOT_ENOUGH_PLACE_INVENTORY));
    }

    private boolean dropHeart(Player player, HeartDropLocation location) {
        World world = player.getWorld();

        switch (location) {
            case GROUND_LEVEL:
                world.dropItemNaturally(player.getLocation(), this.heartItem.getItemStack());
                return true;
            case EYE_LEVEL:
                world.dropItemNaturally(player.getEyeLocation(), this.heartItem.getItemStack());
                return true;
            default:
                return false;
        }
    }

}
