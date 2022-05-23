package io.github.zrdzn.minecraft.greatlifesteal;

import io.github.zrdzn.minecraft.greatlifesteal.config.PluginConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.PluginConfigParser;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartItem;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.SpigotAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.V1_8SpigotAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.V1_9SpigotAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.user.UserListener;
import org.bukkit.Server;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreatLifeStealPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Logger logger = LoggerFactory.getLogger("GreatLifeSteal");

        Server server = this.getServer();

        PluginManager pluginManager = server.getPluginManager();

        this.saveDefaultConfig();
        Configuration configuration = this.getConfig();

        ConfigurationSection baseSection = configuration.getConfigurationSection("baseSettings");
        if (baseSection == null) {
            logger.error("Could not find the 'baseSettings' section.");
            pluginManager.disablePlugin(this);
            return;
        }

        PluginConfig pluginConfig;
        try {
            pluginConfig = new PluginConfigParser().parse(baseSection);
        } catch (InvalidConfigurationException exception) {
            logger.error("Could not parse the 'baseSettings' section.");
            pluginManager.disablePlugin(this);
            return;
        }

        HeartItem heartItem = pluginConfig.getHeartItem();
        if (heartItem != null) server.addRecipe(heartItem.getCraftingRecipe());

        DamageableAdapter damageableAdapter = this.prepareSpigotAdapter().getDamageableAdapter();

        UserListener userListener = new UserListener(pluginConfig, damageableAdapter);

        pluginManager.registerEvents(userListener, this);
    }

    public SpigotAdapter prepareSpigotAdapter() {
        try {
            Class.forName("org.bukkit.attribute.Attributable");
        } catch (ClassNotFoundException exception) {
            return new V1_8SpigotAdapter();
        }

        return new V1_9SpigotAdapter();
    }

}
