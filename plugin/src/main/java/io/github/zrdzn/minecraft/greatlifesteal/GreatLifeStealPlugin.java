package io.github.zrdzn.minecraft.greatlifesteal;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.configs.validator.okaeri.OkaeriValidator;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import io.github.zrdzn.minecraft.greatlifesteal.configs.HeartItemConfig;
import io.github.zrdzn.minecraft.greatlifesteal.configs.PluginConfig;
import io.github.zrdzn.minecraft.greatlifesteal.command.LifeStealCommand;
import io.github.zrdzn.minecraft.greatlifesteal.command.LifeStealTabCompleter;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartItem;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartListener;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.SpigotAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.V1_12SpigotAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.V1_8SpigotAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.V1_9SpigotAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.user.UserListener;
import org.apache.log4j.BasicConfigurator;
import org.bstats.bukkit.Metrics;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class GreatLifeStealPlugin extends JavaPlugin {

    private final Logger logger = LoggerFactory.getLogger("GreatLifeSteal");
    private final Server server = this.getServer();
    private final PluginManager pluginManager = this.server.getPluginManager();

    private PluginConfig config;
    private HeartItem heartItem;
    private SpigotAdapter spigotAdapter;

    public static String formatColor(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static List<String> formatColor(List<String> strings) {
        return strings.stream()
            .map(GreatLifeStealPlugin::formatColor)
            .collect(Collectors.toList());
    }

    @Override
    public void onEnable() {
        BasicConfigurator.configure();

        new Metrics(this, 15277);

        try {
            this.config = ConfigManager.create(PluginConfig.class, (it) -> {
                it.withConfigurer(new OkaeriValidator(new YamlBukkitConfigurer()));
                it.withBindFile(new File(this.getDataFolder(), "config.yml"));
                it.saveDefaults();
                it.load(true);
            });
        } catch (OkaeriException exception) {
            this.logger.error("Could not load the plugin configuration.", exception);
            this.pluginManager.disablePlugin(this);
            return;
        }

        this.spigotAdapter = this.prepareSpigotAdapter();

        if (!this.loadConfigurations()) {
            this.pluginManager.disablePlugin(this);
            return;
        }

        DamageableAdapter damageableAdapter = this.spigotAdapter.getDamageableAdapter();

        boolean latestVersion = this.checkLatestVersion();

        UserListener userListener = new UserListener(this.config, damageableAdapter, this.heartItem, latestVersion);

        this.pluginManager.registerEvents(userListener, this);

        PluginCommand lifeStealCommand = this.getCommand("lifesteal");
        lifeStealCommand.setExecutor(new LifeStealCommand(this, this.config.messages, damageableAdapter, this.server));
        lifeStealCommand.setTabCompleter(new LifeStealTabCompleter(this.config.baseSettings));
    }

    public boolean loadConfigurations() {
        this.saveDefaultConfig();
        this.reloadConfig();

        this.config.load();

        HeartItemConfig heartItemConfig = this.config.baseSettings.heartItem;
        if (heartItemConfig.enabled) {
            ItemStack heartItemStack = new ItemStack(heartItemConfig.type);

            ItemMeta heartItemMeta = heartItemStack.getItemMeta();
            heartItemMeta.setDisplayName(heartItemConfig.meta.getDisplayName());
            heartItemMeta.setLore(heartItemConfig.meta.getLore());
            heartItemStack.setItemMeta(heartItemMeta);

            ShapedRecipe recipe = this.spigotAdapter.getShapedRecipeAdapter().createRecipe(heartItemStack.clone());

            recipe.shape("123", "456", "789");

            heartItemConfig.craftingRecipe.forEach((number, type) -> recipe.setIngredient(number.charAt(0), type));

            this.heartItem = new HeartItem(heartItemConfig.healthAmount, recipe);

            if (!this.server.addRecipe(this.heartItem.getCraftingRecipe())) {
                this.logger.error("Could not add a recipe for some unknown reason.");
            }

            DamageableAdapter adapter = this.spigotAdapter.getDamageableAdapter();

            HeartListener heartListener = new HeartListener(this.config, adapter, this.heartItem);
            this.pluginManager.registerEvents(heartListener, this);
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
