package io.github.zrdzn.minecraft.greatlifesteal.heart;

import java.util.Map;
import io.github.zrdzn.minecraft.greatlifesteal.PluginConfig;
import io.github.zrdzn.minecraft.greatlifesteal.message.MessageFacade;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.PlayerInventoryAdapter;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class HeartFacade {

    private final PluginConfig config;
    private final HeartItem heartItem;
    private final PlayerInventoryAdapter playerInventoryAdapter;

    public HeartFacade(PluginConfig config, HeartItem heartItem, PlayerInventoryAdapter playerInventoryAdapter) {
        this.config = config;
        this.heartItem = heartItem;
        this.playerInventoryAdapter = playerInventoryAdapter;
    }

    public void giveHeartToPlayer(Player player) {
        HeartDropLocation location = this.config.getHeart().getDrop().getLocation();
        if (this.dropHeart(player, location)) {
            return;
        }

        PlayerInventory inventory = player.getInventory();

        Map<Integer, ItemStack> remainingItems = inventory.addItem(this.heartItem.getItemStack());
        if (remainingItems.isEmpty()) {
            return;
        }

        HeartDropLocation fullInventoryLocation = this.config.getHeart().getDrop().getLocationOnFullInventory();
        if (this.dropHeart(player, fullInventoryLocation)) {
            return;
        }

        this.playerInventoryAdapter.removeItem(inventory, this.heartItem.getItemStack());
        MessageFacade.send(player, this.config.getMessages().getNotEnoughPlaceInInventory());
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
