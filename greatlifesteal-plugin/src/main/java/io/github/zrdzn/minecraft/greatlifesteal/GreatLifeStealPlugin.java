package io.github.zrdzn.minecraft.greatlifesteal;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import io.github.zrdzn.minecraft.greatlifesteal.command.LifeStealCommand;
import io.github.zrdzn.minecraft.greatlifesteal.command.LifeStealTabCompleter;
import io.github.zrdzn.minecraft.greatlifesteal.config.ConfigDataBuilder;
import io.github.zrdzn.minecraft.greatlifesteal.config.ConfigMigrationService;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationServiceFactory;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartItem;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartItemFactory;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.SpigotServer;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartConfig;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationRemovalCache;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationService;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationJoinPreventListener;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationRestoreHealthListener;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartCraftListener;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartCraftPrepareListener;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartService;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartUseListener;
import io.github.zrdzn.minecraft.greatlifesteal.placeholderapi.GreatLifeStealExpansion;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.storage.Storage;
import io.github.zrdzn.minecraft.greatlifesteal.storage.StorageFactory;
import io.github.zrdzn.minecraft.greatlifesteal.update.UpdateListener;
import io.github.zrdzn.minecraft.greatlifesteal.update.UpdateNotifier;
import io.github.zrdzn.minecraft.greatlifesteal.user.UserListener;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.log4j.BasicConfigurator;
import org.bstats.bukkit.Metrics;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreatLifeStealPlugin extends JavaPlugin {

    private HeartItem heartItem;

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

        Logger logger = LoggerFactory.getLogger("GreatLifeSteal");

        SpigotServer spigotServer = SpigotServerFactory.createServer(this);
        logger.info("Using {} version of the adapter.", spigotServer.getVersion());

        SettingsManager config = SettingsManagerBuilder
                .withYamlFile(new File(this.getDataFolder(), "config.yml"))
                .configurationData(ConfigDataBuilder.build())
                .migrationService(new ConfigMigrationService())
                .create();
        config.save();

        Server server = this.getServer();
        PluginManager pluginManager = server.getPluginManager();

        if (!this.loadConfigurations(config, logger, spigotServer, server)) {
            pluginManager.disablePlugin(this);
            return;
        }

        if (pluginManager.getPlugin("PlaceholderAPI") == null) {
            logger.warn("PlaceholderAPI plugin has not been found, external placeholders will not work.");
        } else {
            if (new GreatLifeStealExpansion(config, spigotServer.getDamageableAdapter(), server).register()) {
                logger.info("PlaceholderAPI has been found and its expansion was successfully registered.");
            }
        }

        DamageableAdapter damageableAdapter = spigotServer.getDamageableAdapter();

        HeartCraftPrepareListener heartCraftPrepareListener = new HeartCraftPrepareListener(this.heartItem);
        HeartCraftListener heartCraftListener = new HeartCraftListener(this.heartItem);
        HeartUseListener heartUseListener = new HeartUseListener(config, spigotServer, this.heartItem, spigotServer.getNbtService());

        UpdateNotifier updateNotifier = new UpdateNotifier(logger);
        boolean latestVersion = updateNotifier.checkIfLatest(this.getDescription().getVersion());
        UpdateListener updateListener = new UpdateListener(config, latestVersion);

        HeartService heartService = new HeartService(config, this.heartItem, spigotServer.getPlayerInventoryAdapter());

        Storage storage = new StorageFactory(config, logger, this).createStorage();

        EliminationService eliminationService = EliminationServiceFactory.createEliminationService(storage);

        UserListener userListener = new UserListener(this, logger, config, eliminationService, damageableAdapter,
                heartService, this.heartItem);

        EliminationRemovalCache eliminationRemovalCache = new EliminationRemovalCache();

        EliminationJoinPreventListener eliminationJoinPreventListener = new EliminationJoinPreventListener(logger, config,
                eliminationService, eliminationRemovalCache);

        EliminationRestoreHealthListener eliminationRestoreHealthListener = new EliminationRestoreHealthListener(logger,
                config, eliminationService, spigotServer.getDamageableAdapter(), eliminationRemovalCache);

        pluginManager.registerEvents(updateListener, this);
        pluginManager.registerEvents(userListener, this);
        pluginManager.registerEvents(eliminationJoinPreventListener, this);
        pluginManager.registerEvents(eliminationRestoreHealthListener, this);
        pluginManager.registerEvents(heartCraftPrepareListener, this);
        pluginManager.registerEvents(heartCraftListener, this);
        pluginManager.registerEvents(heartUseListener, this);

        PluginCommand lifeStealCommand = this.getCommand("lifesteal");
        lifeStealCommand.setExecutor(new LifeStealCommand(this, logger, config, eliminationService, damageableAdapter, spigotServer, this.heartItem));
        lifeStealCommand.setTabCompleter(new LifeStealTabCompleter(config));
    }

    public boolean loadConfigurations(SettingsManager config, Logger logger, SpigotServer spigotServer, Server server) {
        this.saveDefaultConfig();
        this.reloadConfig();

        config.reload();

        if (config.getProperty(HeartConfig.ENABLED)) {
            HeartItem heartItem = new HeartItemFactory(config, logger, spigotServer).createHeartItem();

            ShapedRecipe recipe = heartItem.getRecipe();

            if (spigotServer.getRecipeManagerAdapter().removeServerShapedRecipe(recipe)) {
                logger.info("Removed the old heart item recipe.");
            }

            if (server.addRecipe(recipe)) {
                logger.info("Added the new heart item recipe.");
            } else {
                logger.error("Could not add the new heart item recipe for some unknown reason.");
            }

            this.heartItem = heartItem;
        }

        return true;
    }

}
