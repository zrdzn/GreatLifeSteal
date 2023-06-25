package io.github.zrdzn.minecraft.greatlifesteal.storage;

import ch.jalu.configme.SettingsManager;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;

public class StorageFactory {

    private final SettingsManager config;
    private final Logger logger;
    private final Plugin plugin;

    public StorageFactory(SettingsManager config, Logger logger, Plugin plugin) {
        this.config = config;
        this.logger = logger;
        this.plugin = plugin;
    }

    public Storage createStorage() {
        switch (this.config.getProperty(StorageConfig.TYPE)) {
            case MYSQL:
                return new MysqlStorage(this.logger).load(this.config)
                        .peek(ignored -> this.logger.info("Choosing MySQL as a storage provider."))
                        .onError(error -> {
                            this.logger.error("Something went wrong while loading the MySQL storage. Check if your credentials are correct and then restart the server.", error);
                            this.plugin.getPluginLoader().disablePlugin(this.plugin);
                        })
                        .get();
            case SQLITE:
                try {
                    Class.forName("org.sqlite.JDBC");
                } catch (ClassNotFoundException exception) {
                    this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
                    throw new RuntimeException("SQLite driver not found.", exception);
                }

                return new SqliteStorage(this.plugin.getDataFolder()).load(this.config)
                        .peek(ignored -> this.logger.info("Choosing SQLite as a storage provider."))
                        .onError(error -> {
                            this.logger.error("Something went wrong while loading the SQLite storage. Check if your credentials are correct and then restart the server.", error);
                            this.plugin.getPluginLoader().disablePlugin(this.plugin);
                        })
                        .get();
            default:
                throw new IllegalArgumentException("There is no such storage type.");
        }
    }

}
