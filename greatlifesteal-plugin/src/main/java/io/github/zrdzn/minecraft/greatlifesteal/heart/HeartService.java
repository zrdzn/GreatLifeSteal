package io.github.zrdzn.minecraft.greatlifesteal.heart;

import java.util.Map;
import ch.jalu.configme.SettingsManager;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.MessagesConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.heart.HeartDropConfig;
import io.github.zrdzn.minecraft.greatlifesteal.message.MessageService;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HeartService {

    private final SettingsManager config;
    private final HeartItem heartItem;

    public HeartService(SettingsManager config, HeartItem heartItem) {
        this.config = config;
        this.heartItem = heartItem;
    }

    public void giveHeartToPlayer(Player player) {
        World world = player.getWorld();

        HeartDropLocation location = this.config.getProperty(HeartDropConfig.LOCATION);
        if (this.dropHeart(player, location)) {
            return;
        }

        Map<Integer, ItemStack> remainingItems = player.getInventory().addItem(this.heartItem.result);
        if (remainingItems.isEmpty()) {
            return;
        }

        HeartDropLocation fullInventoryLocation = this.config.getProperty(HeartDropConfig.FULL_INVENTORY_LOCATION);
        if (this.dropHeart(player, fullInventoryLocation)) {
            return;
        }

        MessageService.send(player, this.config.getProperty(MessagesConfig.NOT_ENOUGH_PLACE_INVENTORY));
    }

    private boolean dropHeart(Player player, HeartDropLocation location) {
        World world = player.getWorld();

        switch (location) {
            case GROUND_LEVEL:
                world.dropItemNaturally(player.getLocation(), this.heartItem.result);
                return true;
            case EYE_LEVEL:
                world.dropItemNaturally(player.getEyeLocation(), this.heartItem.result);
                return true;
            default:
                return false;
        }
    }

}
