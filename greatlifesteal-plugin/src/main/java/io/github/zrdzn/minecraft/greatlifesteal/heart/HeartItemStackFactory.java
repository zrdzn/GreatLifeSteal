package io.github.zrdzn.minecraft.greatlifesteal.heart;

import ch.jalu.configme.SettingsManager;
import io.github.zrdzn.minecraft.greatlifesteal.heart.configs.HeartConfig;
import io.github.zrdzn.minecraft.greatlifesteal.heart.configs.HeartMetaConfig;
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

        this.logger.info("Applying NBT to heart item.");
        nbtService.setBoolean(itemStack, "heart", true);
        nbtService.setDouble(itemStack, "heartHealthChange", this.config.getProperty(HeartConfig.HEALTH_AMOUNT));

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
