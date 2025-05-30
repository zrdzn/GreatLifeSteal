package io.github.zrdzn.minecraft.greatlifesteal.heart;

import io.github.zrdzn.minecraft.greatlifesteal.spigot.NbtService;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.SpigotServer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static io.github.zrdzn.minecraft.greatlifesteal.GreatLifeStealPlugin.formatColor;

public class HeartItemStackFactory {

    private final HeartConfig config;
    private final SpigotServer spigotServer;

    public HeartItemStackFactory(HeartConfig config, SpigotServer spigotServer) {
        this.config = config;
        this.spigotServer = spigotServer;
    }

    public ItemStack createHeartItemStack() {
        ItemStack itemStack = new ItemStack(this.config.getType());

        NbtService nbtService = this.spigotServer.getNbtService();

        nbtService.setBoolean(itemStack, HeartItem.HEART_NBT_KEY, true);
        nbtService.setDouble(itemStack, HeartItem.HEART_HEALTH_CHANGE_NBT_KEY, this.config.getHealthAmount());

        ItemMeta heartItemMeta = itemStack.getItemMeta();

        heartItemMeta.setDisplayName(formatColor(this.config.getMeta().getDisplayName()));
        heartItemMeta.setLore(formatColor(this.config.getMeta().getLore()));

        if (this.config.getMeta().isGlowing()) {
            heartItemMeta.addEnchant(Enchantment.LURE, 1, false);
            heartItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        itemStack.setItemMeta(heartItemMeta);

        return itemStack;
    }

}
