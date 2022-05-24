package io.github.zrdzn.minecraft.greatlifesteal;

import io.github.zrdzn.minecraft.greatlifesteal.config.PluginConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.PluginConfigParser;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartItem;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.SpigotAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.V1_8SpigotAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.V1_9SpigotAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.user.UserListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GreatLifeStealPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Logger logger = LoggerFactory.getLogger("GreatLifeSteal");

        new Metrics(this, 15277);

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
        if (heartItem != null) {
            server.addRecipe(heartItem.getCraftingRecipe());
        }

        DamageableAdapter damageableAdapter = this.prepareSpigotAdapter().getDamageableAdapter();

        UserListener userListener = new UserListener(pluginConfig, damageableAdapter);

        pluginManager.registerEvents(userListener, this);

        this.checkPluginUpdates(logger);
    }

    public SpigotAdapter prepareSpigotAdapter() {
        try {
            Class.forName("org.bukkit.attribute.Attributable");
        } catch (ClassNotFoundException exception) {
            return new V1_8SpigotAdapter();
        }

        return new V1_9SpigotAdapter();
    }

    private void checkPluginUpdates(Logger logger) {
        URL url;
        try {
            url = new URL("https://api.spigotmc.org/simple/0.1/index.php?action=getResource&id=102206");
        } catch (MalformedURLException exception) {
            logger.error("Could not get the resource from spigotmc.", exception);
            return;
        }

        HttpURLConnection http;
        try {
            http = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
            StringBuilder response = new StringBuilder();

            String readLine;
            while ((readLine = reader.readLine()) != null) {
                response.append(readLine);
            } reader.close();

            JSONParser parser = new JSONParser();

            String latestVersion;
            try {
                JSONObject json = (JSONObject) parser.parse(response.toString());
                latestVersion = (String) json.get("current_version");
            } catch (ParseException exception) {
                logger.error("Could not parse the json string.", exception);
                return;
            }

            if (latestVersion == null) {
                logger.error("Something went wrong while getting a version.");
                return;
            }

            if (!latestVersion.equals(this.getDescription().getVersion())) {
                logger.warn("You are using an outdated version of the plugin!");
                logger.warn("You can download the latest one here:");
                logger.warn("https://www.spigotmc.org/resources/greatlifesteal.102206/");
            }
        } catch (IOException exception) {
            logger.error("Could not read from the json body.", exception);
            return;
        }

        http.disconnect();
    }

}
