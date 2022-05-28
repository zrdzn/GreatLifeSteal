package io.github.zrdzn.minecraft.greatlifesteal;

import io.github.zrdzn.minecraft.greatlifesteal.config.PluginConfig;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartItem;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartListener;
import io.github.zrdzn.minecraft.greatlifesteal.message.MessageCache;
import io.github.zrdzn.minecraft.greatlifesteal.message.MessageLoader;
import io.github.zrdzn.minecraft.greatlifesteal.message.MessageService;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.SpigotAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.V1_12SpigotAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.V1_8SpigotAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.V1_9SpigotAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.user.UserListener;
import org.apache.log4j.BasicConfigurator;
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
import java.lang.reflect.Constructor;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GreatLifeStealPlugin extends JavaPlugin {

    private final Logger logger = LoggerFactory.getLogger("GreatLifeSteal");
    private final PluginConfig pluginConfig = new PluginConfig();
    private final MessageCache messageCache = new MessageCache();
    private final MessageLoader messageLoader = new MessageLoader(this.messageCache);
    private final MessageService messageService = new MessageService(this.messageCache);
    private final Server server = this.getServer();
    private final PluginManager pluginManager = this.server.getPluginManager();

    private SpigotAdapter spigotAdapter;

    @Override
    public void onEnable() {
        BasicConfigurator.configure();

        new Metrics(this, 15277);

        if (!this.loadConfigurations()) {
            this.pluginManager.disablePlugin(this);
            return;
        }

        this.spigotAdapter = this.prepareSpigotAdapter();

        DamageableAdapter damageableAdapter = this.spigotAdapter.getDamageableAdapter();

        boolean latestVersion = this.checkLatestVersion();

        UserListener userListener = new UserListener(this.pluginConfig, this.messageService, damageableAdapter, latestVersion);

        this.pluginManager.registerEvents(userListener, this);

        this.getCommand("lifesteal").setExecutor(new LifeStealCommand(this, this.messageService, damageableAdapter, this.server));
    }

    public boolean loadConfigurations() {
        this.saveDefaultConfig();
        this.reloadConfig();

        Configuration configuration = this.getConfig();

        try {
            ConfigurationSection section = configuration.getConfigurationSection("baseSettings");
            if (section == null) {
                this.logger.error("Could not find the 'baseSettings' section.");
                return false;
            }

            this.pluginConfig.parseAndLoad(section);
        } catch (InvalidConfigurationException exception) {
            this.logger.error("Could not load the plugin configuration.", exception);
            return false;
        }

        HeartItem heartItem = this.pluginConfig.heartItem;
        if (heartItem != null) {
            if (!this.server.addRecipe(heartItem.getCraftingRecipe())) {
                this.logger.error("Could not add a recipe for some unknown reason.");
            }

            DamageableAdapter adapter = this.spigotAdapter.getDamageableAdapter();

            HeartListener heartListener = new HeartListener(this.pluginConfig, adapter, this.messageService, heartItem);
            this.pluginManager.registerEvents(heartListener, this);
        }

        try {
            ConfigurationSection section = configuration.getConfigurationSection("messages");
            if (section == null) {
                this.logger.error("Could not find the 'messages' section.");
                return false;
            }

            this.messageLoader.load(section);
        } catch (InvalidConfigurationException exception) {
            this.logger.error("Could not load messages from the plugin configuration.", exception);
            return false;
        }

        return true;
    }

    public SpigotAdapter prepareSpigotAdapter() {
        try {
            Class.forName("org.bukkit.attribute.Attributable");
        } catch (ClassNotFoundException exception) {
            return new V1_8SpigotAdapter();
        }

        try {
            for (Constructor<?> constructor : Class.forName("org.bukkit.inventory.ShapedRecipe").getDeclaredConstructors()) {
                if (constructor.getParameterCount() == 2) {
                    return new V1_12SpigotAdapter(this);
                }
            }

            return new V1_9SpigotAdapter();
        } catch (ClassNotFoundException exception) {
            this.logger.error("Could not find the ShapedRecipe class.", exception);
            this.pluginManager.disablePlugin(this);
            return null;
        }
    }

    /**
     * Checks if the plugin is using the latest version.
     *
     * @return true if plugin is using the latest version or an error occurred, false if it uses an older version
     */
    private boolean checkLatestVersion() {
        URL url;
        try {
            url = new URL("https://api.spigotmc.org/simple/0.1/index.php?action=getResource&id=102206");
        } catch (MalformedURLException exception) {
            this.logger.warn("Update notifier: Could not get the resource from spigotmc.");
            return true;
        }

        HttpURLConnection http;
        try {
            http = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
            StringBuilder response = new StringBuilder();

            String readLine;
            while ((readLine = reader.readLine()) != null) {
                response.append(readLine);
            }
            reader.close();

            JSONParser parser = new JSONParser();

            String latestVersion;
            try {
                JSONObject json = (JSONObject) parser.parse(response.toString());
                latestVersion = (String) json.get("current_version");
            } catch (ParseException exception) {
                this.logger.warn("Update notifier: Could not parse the json string.");
                return true;
            }

            if (latestVersion == null) {
                this.logger.warn("Update notifier: Something went wrong while getting a version.");
                return true;
            }

            if (!latestVersion.equals(this.getDescription().getVersion())) {
                this.logger.warn("You are using an outdated version of the plugin!");
                this.logger.warn("You can download the latest one here:");
                this.logger.warn("https://www.spigotmc.org/resources/greatlifesteal.102206/");

                http.disconnect();

                return false;
            }

            return true;
        } catch (IOException exception) {
            this.logger.warn("Update notifier: Could not read from the json body.");
            return true;
        }
    }

}
