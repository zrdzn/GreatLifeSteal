package io.github.zrdzn.minecraft.greatlifesteal;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import io.github.zrdzn.minecraft.greatlifesteal.command.LifeStealCommand;
import io.github.zrdzn.minecraft.greatlifesteal.command.LifeStealTabCompleter;
import io.github.zrdzn.minecraft.greatlifesteal.config.ConfigDataBuilder;
import io.github.zrdzn.minecraft.greatlifesteal.config.ConfigMigrationService;
import io.github.zrdzn.minecraft.greatlifesteal.config.bean.beans.BasicItemBean;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.DataSourceConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.heart.HeartConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.heart.HeartMetaConfig;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationRemovalCache;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationRepository;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationService;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.listeners.EliminationJoinPreventListener;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.listeners.EliminationRestoreHealthListener;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.repositories.MysqlEliminationRepository;
import io.github.zrdzn.minecraft.greatlifesteal.heart.listeners.HeartCraftListener;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartItem;
import io.github.zrdzn.minecraft.greatlifesteal.heart.listeners.HeartCraftPrepareListener;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartService;
import io.github.zrdzn.minecraft.greatlifesteal.heart.listeners.HeartUseListener;
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
import io.github.zrdzn.minecraft.greatlifesteal.storage.Storage;
import io.github.zrdzn.minecraft.greatlifesteal.storage.StorageType;
import io.github.zrdzn.minecraft.greatlifesteal.storage.storages.MysqlStorage;
import io.github.zrdzn.minecraft.greatlifesteal.storage.storages.SqliteStorage;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.repositories.SqliteEliminationRepository;
import io.github.zrdzn.minecraft.greatlifesteal.update.UpdateListener;
import io.github.zrdzn.minecraft.greatlifesteal.update.UpdateNotifier;
import io.github.zrdzn.minecraft.greatlifesteal.user.UserListener;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.log4j.BasicConfigurator;
import org.bstats.bukkit.Metrics;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreatLifeStealPlugin extends JavaPlugin {

    private final Logger logger = LoggerFactory.getLogger("GreatLifeSteal");
    private final Server server = this.getServer();
    private final PluginManager pluginManager = this.server.getPluginManager();
    private final HeartItem heartItem = new HeartItem();

    private SettingsManager config;
    private SpigotAdapter spigotAdapter;

    private Storage storage;
    private EliminationService eliminationService;

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

        if (this.pluginManager.getPlugin("PlaceholderAPI") == null) {
            this.logger.warn("PlaceholderAPI plugin has not been found, external placeholders will not work.");
        } else {
            if (new GreatLifeStealExpansion(this.config, this.spigotAdapter.getDamageableAdapter(),
                    this.server).register()) {
                this.logger.info("PlaceholderAPI has been found and its expansion was successfully registered.");
            }
        }

        DamageableAdapter damageableAdapter = this.spigotAdapter.getDamageableAdapter();

        HeartCraftPrepareListener heartCraftPrepareListener = new HeartCraftPrepareListener(this.heartItem);
        HeartCraftListener heartCraftListener = new HeartCraftListener(this.heartItem);
        HeartUseListener heartUseListener = new HeartUseListener(this.config, this.spigotAdapter, this.heartItem);

        UpdateNotifier updateNotifier = new UpdateNotifier(this.logger);
        boolean latestVersion = updateNotifier.checkIfLatest(this.getDescription().getVersion());
        UpdateListener updateListener = new UpdateListener(this.config, latestVersion);

        HeartService heartService = new HeartService(this.config, this.heartItem, this.spigotAdapter.getPlayerInventoryAdapter());

        UserListener userListener = new UserListener(this, this.logger, this.config, this.eliminationService,
                damageableAdapter, heartService, this.heartItem);

        EliminationRemovalCache eliminationRemovalCache = new EliminationRemovalCache();

        EliminationJoinPreventListener eliminationJoinPreventListener = new EliminationJoinPreventListener(this.logger,
                this.config, this.eliminationService, eliminationRemovalCache);

        EliminationRestoreHealthListener eliminationRestoreHealthListener = new EliminationRestoreHealthListener(this.logger,
                this.config, this.eliminationService, this.spigotAdapter.getDamageableAdapter(), eliminationRemovalCache);

        this.pluginManager.registerEvents(updateListener, this);
        this.pluginManager.registerEvents(userListener, this);
        this.pluginManager.registerEvents(eliminationJoinPreventListener, this);
        this.pluginManager.registerEvents(eliminationRestoreHealthListener, this);
        this.pluginManager.registerEvents(heartCraftPrepareListener, this);
        this.pluginManager.registerEvents(heartCraftListener, this);
        this.pluginManager.registerEvents(heartUseListener, this);

        PluginCommand lifeStealCommand = this.getCommand("lifesteal");
        lifeStealCommand.setExecutor(new LifeStealCommand(this, this.logger, this.config, this.eliminationService,
                damageableAdapter, this.heartItem));
        lifeStealCommand.setTabCompleter(new LifeStealTabCompleter(this.config));
    }

    @Override
    public void onDisable() {
        this.storage.stop();
    }

    public boolean loadConfigurations() {
        this.saveDefaultConfig();
        this.reloadConfig();

        this.config.reload();

        this.loadDataSource();

        if (this.config.getProperty(HeartConfig.ENABLED)) {
            ItemStack heartItemStack = new ItemStack(this.config.getProperty(HeartConfig.TYPE));

            ItemMeta heartItemMeta = heartItemStack.getItemMeta();
            heartItemMeta.setDisplayName(formatColor(this.config.getProperty(HeartMetaConfig.DISPLAY_NAME)));
            heartItemMeta.setLore(formatColor(this.config.getProperty(HeartMetaConfig.LORE)));
            if (this.config.getProperty(HeartMetaConfig.GLOWING)) {
                heartItemMeta.addEnchant(Enchantment.LURE, 1, false);
                heartItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
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

    public void loadDataSource() {
        EliminationRepository eliminationRepository = null;

        StorageType type = this.config.getProperty(DataSourceConfig.TYPE);
        if (type == StorageType.SQLITE) {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException exception) {
                this.logger.error("Could not find a driver for the sqlite data source.", exception);
                this.pluginManager.disablePlugin(this);
                return;
            }

            this.storage = new SqliteStorage(this.getDataFolder()).load(this.config)
                    .peek(ignored -> this.logger.info("Choosing SQLite as a storage provider."))
                    .onError(error -> {
                        this.logger.error("Something went wrong while loading the SQLite storage. Check if your credentials are correct and then restart the server.", error);
                        this.pluginManager.disablePlugin(this);
                    })
                    .get();

            eliminationRepository = new SqliteEliminationRepository((SqliteStorage) this.storage);
        } else if (type == StorageType.MYSQL) {
            this.storage = new MysqlStorage(this.logger).load(this.config)
                    .peek(ignored -> this.logger.info("Choosing MySQL as a storage provider."))
                    .onError(error -> {
                        this.logger.error("Something went wrong while loading the MySQL storage. Check if your credentials are correct and then restart the server.", error);
                        this.pluginManager.disablePlugin(this);
                    })
                    .get();

            eliminationRepository = new MysqlEliminationRepository((MysqlStorage) this.storage);
        }

        this.eliminationService = new EliminationService(eliminationRepository);
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
            case "v1_19_R2":
                return new V1_15R1SpigotAdapter(this);
            default:
                throw new RuntimeException(
                        "Could not find an adapter for the version: " + version + "\n" +
                        "Check supported versions on the resource page."
                );
        }
    }

}
