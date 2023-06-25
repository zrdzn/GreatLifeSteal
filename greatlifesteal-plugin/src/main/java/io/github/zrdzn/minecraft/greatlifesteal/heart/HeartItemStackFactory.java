package io.github.zrdzn.minecraft.greatlifesteal.heart;

import ch.jalu.configme.SettingsManager;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.NbtService;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.SpigotServer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.slf4j.Logger;

import static io.github.zrdzn.minecraft.greatlifesteal.GreatLifeStealPlugin.formatColor;

public class HeartItemStackFactory {

    private final SettingsManager config;
    private final Logger logger;
    private final SpigotServer spigotServer;

    public HeartItemStackFactory(SettingsManager config, Logger logger, SpigotServer spigotServer) {
        this.config = config;
        this.logger = logger;
        this.spigotServer = spigotServer;
    }

    public ItemStack createHeartItemStack() {
        ItemStack itemStack = new ItemStack(this.config.getProperty(HeartConfig.TYPE));

        NbtService nbtService = this.spigotServer.getNbtService();

        this.logger.info("Applying '{}' nbt key to heart item.", HeartItem.HEART_NBT_KEY);
        nbtService.setBoolean(itemStack, HeartItem.HEART_NBT_KEY, true);
        this.logger.info("Applying '{}' nbt key to heart item.", HeartItem.HEART_HEALTH_CHANGE_NBT_KEY);
        nbtService.setDouble(itemStack, HeartItem.HEART_HEALTH_CHANGE_NBT_KEY, this.config.getProperty(HeartConfig.HEALTH_AMOUNT));

        ItemMeta heartItemMeta = itemStack.getItemMeta();

        heartItemMeta.setDisplayName(formatColor(this.config.getProperty(HeartMetaConfig.DISPLAY_NAME)));
        heartItemMeta.setLore(formatColor(this.config.getProperty(HeartMetaConfig.LORE)));

        if (this.config.getProperty(HeartMetaConfig.GLOWING)) {
            heartItemMeta.addEnchant(Enchantment.LURE, 1, false);
            heartItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        itemStack.setItemMeta(heartItemMeta);

        return itemStack;
    }

}
