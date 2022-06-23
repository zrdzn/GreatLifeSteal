package io.github.zrdzn.minecraft.greatlifesteal;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import io.github.zrdzn.minecraft.greatlifesteal.command.LifeStealCommand;
import io.github.zrdzn.minecraft.greatlifesteal.command.LifeStealTabCompleter;
import io.github.zrdzn.minecraft.greatlifesteal.config.ConfigDataBuilder;
import io.github.zrdzn.minecraft.greatlifesteal.config.ConfigMigrationService;
import io.github.zrdzn.minecraft.greatlifesteal.config.bean.beans.BasicItemBean;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.heart.HeartConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.heart.HeartMetaConfig;
import io.github.zrdzn.minecraft.greatlifesteal.health.HealthCache;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartItem;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartListener;
import io.github.zrdzn.minecraft.greatlifesteal.placeholderapi.GreatLifeStealExpansion;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.SpigotAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.V1_10R1SpigotAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.V1_11R1SpigotAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.V1_12R1SpigotAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.V1_13R2SpigotAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.V1_14R1SpigotAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.V1_15R1SpigotAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.V1_8R3SpigotAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.V1_9R2SpigotAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.user.UserListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.log4j.BasicConfigurator;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

public class GreatLifeStealPlugin extends JavaPlugin {

    private final Logger logger = LoggerFactory.getLogger("GreatLifeSteal");
    private final Server server = this.getServer();
    private final PluginManager pluginManager = this.server.getPluginManager();
    private final HeartItem heartItem = new HeartItem();

    private SettingsManager config;
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

        this.spigotAdapter = this.prepareSpigotAdapter();
        this.logger.info("Using {} version of the adapter.", this.spigotAdapter.getVersion());

        this.config = SettingsManagerBuilder
                .withYamlFile(new File(this.getDataFolder(), "config.yml"))
                .configurationData(ConfigDataBuilder.build())
                .migrationService(new ConfigMigrationService())
                .create();
        this.config.save();

        if (!this.loadConfigurations()) {
            this.pluginManager.disablePlugin(this);
            return;
        }

        HealthCache healthCache = new HealthCache(this.logger);
        if (this.pluginManager.getPlugin("PlaceholderAPI") == null) {
            this.logger.warn("PlaceholderAPI plugin has not been found, external placeholders will not work.");
        } else {
            /* (PAPI) Until .dat files are parsed correctly depending on the version, we cannot support offline players in placeholders.

            Optional<File> playerDataMaybe = this.server.getWorlds().stream()

                .map(world -> new File(this.server.getWorldContainer() + "/" + world.getName() + "/playerdata"))
                .filter(File::exists)
                .filter(File::isDirectory)
                .findAny();

            if (!playerDataMaybe.isPresent()) {
                this.logger.error("Could not find the world that stores 'playerdata' directory.");
                this.pluginManager.disablePlugin(this);
                return;
            }

            if (healthCache.loadFromFiles(playerDataMaybe.get())) {
                if (new GreatLifeStealExpansion(this.config.baseSettings, this.spigotAdapter.getDamageableAdapter(),
                    this.server, healthCache).register()) {
                    this.logger.info("PlaceholderAPI has been found and its expansion was successfully registered.");
                }
            }
            */
            if (new GreatLifeStealExpansion(this.config, this.spigotAdapter.getDamageableAdapter(),
                    this.server, healthCache).register()) {
                this.logger.info("PlaceholderAPI has been found and its expansion was successfully registered.");
            }
        }

        DamageableAdapter damageableAdapter = this.spigotAdapter.getDamageableAdapter();

        HeartListener heartListener = new HeartListener(this.config, damageableAdapter, this.heartItem);
        this.pluginManager.registerEvents(heartListener, this);

        boolean latestVersion = this.checkLatestVersion();

        UserListener userListener = new UserListener(this.config, damageableAdapter, healthCache, this.heartItem,
                latestVersion);

        this.pluginManager.registerEvents(userListener, this);

        PluginCommand lifeStealCommand = this.getCommand("lifesteal");
        lifeStealCommand.setExecutor(new LifeStealCommand(this, this.config, damageableAdapter, this.heartItem, this.server));
        lifeStealCommand.setTabCompleter(new LifeStealTabCompleter(this.config));
    }

    public boolean loadConfigurations() {
        this.saveDefaultConfig();
        this.reloadConfig();

        this.config.reload();

        if (this.config.getProperty(HeartConfig.ENABLED)) {
            ItemStack heartItemStack = new ItemStack(this.config.getProperty(HeartConfig.TYPE));

            ItemMeta heartItemMeta = heartItemStack.getItemMeta();
            heartItemMeta.setDisplayName(formatColor(this.config.getProperty(HeartMetaConfig.DISPLAY_NAME)));
            heartItemMeta.setLore(formatColor(this.config.getProperty(HeartMetaConfig.LORE)));
            heartItemStack.setItemMeta(heartItemMeta);

            ShapedRecipe recipe = this.spigotAdapter.getShapedRecipeAdapter().createShapedRecipe(heartItemStack);
            recipe.shape("123", "456", "789");

            Map<Integer, ItemStack> ingredients = new HashMap<>();
            for (Entry<String, BasicItemBean> item : this.config.getProperty(HeartConfig.CRAFTING).entrySet()) {
                String slotRaw = item.getKey();
                int slot;
                try {
                    slot = Integer.parseUnsignedInt(slotRaw);
                } catch (NumberFormatException exception) {
                    this.logger.warn("Could not parse the {} slot, because it is not a positive integer.", slotRaw);
                    continue;
                }

                BasicItemBean recipeItem = item.getValue();
                Material recipeItemType = recipeItem.getType();

                recipe.setIngredient(slotRaw.charAt(0), recipeItemType);
                ingredients.put(slot, new ItemStack(recipeItemType, recipeItem.getAmount()));
            }

            if (this.spigotAdapter.getRecipeManagerAdapter().removeServerShapedRecipe(recipe)) {
                this.logger.info("Removed the old heart item recipe.");
            }

            if (this.server.addRecipe(recipe)) {
                this.logger.info("Added the new heart item recipe.");
            } else {
                this.logger.error("Could not add the new heart item recipe for some unknown reason.");
            }

            this.heartItem.healthAmount = this.config.getProperty(HeartConfig.HEALTH_AMOUNT);
            this.heartItem.result = heartItemStack.clone();
            this.heartItem.ingredients = ingredients;
        }

        return true;
    }

    private SpigotAdapter prepareSpigotAdapter() {
        String version = this.server.getClass().getPackage().getName().split("\\.")[3];
        switch (version) {
            case "v1_8_R3":
                return new V1_8R3SpigotAdapter();
            case "v1_9_R2":
                return new V1_9R2SpigotAdapter();
            case "v1_10_R1":
                return new V1_10R1SpigotAdapter();
            case "v1_11_R1":
                return new V1_11R1SpigotAdapter();
            case "v1_12_R1":
                return new V1_12R1SpigotAdapter(this);
            case "v1_13_R2":
                return new V1_13R2SpigotAdapter(this);
            case "v1_14_R1":
                return new V1_14R1SpigotAdapter(this);
            case "v1_15_R1":
            case "v1_16_R3":
            case "v1_17_R1":
            case "v1_18_R2":
            case "v1_19_R1":
                return new V1_15R1SpigotAdapter(this);
            default:
                throw new RuntimeException(
                        "Could not find an adapter for the version: " + version + "\n" +
                        "Check supported versions on the resource page."
                );
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
