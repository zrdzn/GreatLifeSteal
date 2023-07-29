package io.github.zrdzn.minecraft.greatlifesteal;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import dev.piotrulla.craftinglib.CraftingException;
import dev.piotrulla.craftinglib.CraftingLib;
import dev.piotrulla.craftinglib.CraftingManager;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.configs.validator.okaeri.OkaeriValidator;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import io.github.zrdzn.minecraft.greatlifesteal.command.LifeStealCommand;
import io.github.zrdzn.minecraft.greatlifesteal.command.LifeStealTabCompleter;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationFacade;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationFacadeFactory;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationJoinPreventListener;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationRemovalCache;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationRestoreHealthListener;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartFacade;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartItem;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartItemFactory;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartUseListener;
import io.github.zrdzn.minecraft.greatlifesteal.placeholderapi.GreatLifeStealExpansion;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.SpigotServer;
import io.github.zrdzn.minecraft.greatlifesteal.storage.Storage;
import io.github.zrdzn.minecraft.greatlifesteal.storage.StorageFactory;
import io.github.zrdzn.minecraft.greatlifesteal.update.UpdateListener;
import io.github.zrdzn.minecraft.greatlifesteal.update.UpdateNotifier;
import io.github.zrdzn.minecraft.greatlifesteal.user.UserListener;
import org.apache.log4j.BasicConfigurator;
import org.bstats.bukkit.Metrics;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreatLifeStealPlugin extends JavaPlugin {

    private final Logger logger = LoggerFactory.getLogger(GreatLifeStealPlugin.class);

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

        SpigotServer spigotServer = new SpigotServerFactory(this).createServer();
        this.logger.info("Using {} version of the adapter.", spigotServer.getVersion());

        Server server = this.getServer();
        PluginManager pluginManager = server.getPluginManager();

        PluginConfig config;
        try {
            config = ConfigManager.create(PluginConfig.class, (it) -> {
                it.withConfigurer(new OkaeriValidator(new YamlBukkitConfigurer()));
                it.withBindFile(new File(this.getDataFolder(), "config.yml"));
                it.withRemoveOrphans(true);
                it.saveDefaults();
                it.load(true);
            });
        } catch (OkaeriException exception) {
            this.logger.error("Could not load the plugin configuration.", exception);
            pluginManager.disablePlugin(this);
            return;
        }

        this.loadConfigurations(config, spigotServer);

        if (pluginManager.getPlugin("PlaceholderAPI") == null) {
            this.logger.warn("PlaceholderAPI plugin has not been found, external placeholders will not work.");
        } else {
            if (new GreatLifeStealExpansion(config, spigotServer.getDamageableAdapter(), server).register()) {
                this.logger.info("PlaceholderAPI has been found and its expansion was successfully registered.");
            }
        }

        DamageableAdapter damageableAdapter = spigotServer.getDamageableAdapter();

        HeartUseListener heartUseListener = new HeartUseListener(config, spigotServer, this.heartItem, spigotServer.getNbtService());

        UpdateNotifier updateNotifier = new UpdateNotifier();
        boolean latestVersion = updateNotifier.checkIfLatest(this.getDescription().getVersion());
        UpdateListener updateListener = new UpdateListener(config.getMessages(), latestVersion);

        HeartFacade heartFacade = new HeartFacade(config, this.heartItem, spigotServer.getPlayerInventoryAdapter());

        Storage storage = new StorageFactory(config.getStorage(), this).createStorage();

        EliminationFacade eliminationFacade = new EliminationFacadeFactory(storage).createEliminationFacade();

        UserListener userListener = new UserListener(this, config, eliminationFacade, damageableAdapter, heartFacade, this.heartItem);

        EliminationRemovalCache eliminationRemovalCache = new EliminationRemovalCache();

        EliminationJoinPreventListener eliminationJoinPreventListener = new EliminationJoinPreventListener(this, config,
                eliminationFacade, eliminationRemovalCache);

        EliminationRestoreHealthListener eliminationRestoreHealthListener = new EliminationRestoreHealthListener(this, config,
                eliminationFacade, spigotServer.getDamageableAdapter(), eliminationRemovalCache);

        pluginManager.registerEvents(updateListener, this);
        pluginManager.registerEvents(userListener, this);
        pluginManager.registerEvents(eliminationJoinPreventListener, this);
        pluginManager.registerEvents(eliminationRestoreHealthListener, this);
        pluginManager.registerEvents(heartUseListener, this);

        PluginCommand lifeStealCommand = this.getCommand("lifesteal");
        lifeStealCommand.setExecutor(new LifeStealCommand(this, config, eliminationFacade, damageableAdapter, spigotServer, this.heartItem));
        lifeStealCommand.setTabCompleter(new LifeStealTabCompleter(config));
    }

    public void loadConfigurations(PluginConfig config, SpigotServer spigotServer) {
        this.saveDefaultConfig();
        this.reloadConfig();
        config.load();

        if (config.getHeart().isEnabled()) {
            HeartItem heartItem = new HeartItemFactory(config.getHeart(), spigotServer).createHeartItem();

            CraftingManager craftingManager = new CraftingLib(this).getCraftingManager();

            try {
                craftingManager.removeCrafting(HeartItem.HEART_CRAFTING_NAME);
                this.logger.info("Removed the old heart item recipe.");
            } catch (CraftingException ignored) {}

            try {
                craftingManager.createCrafting(HeartItem.HEART_CRAFTING_NAME, heartItem.getCraftingRecipe());
                this.logger.info("Added the new heart item recipe.");
            } catch (CraftingException exception) {
                this.logger.error("Heart recipe already exists.", exception);
                return;
            }

            this.heartItem = heartItem;
        }
    }

}
