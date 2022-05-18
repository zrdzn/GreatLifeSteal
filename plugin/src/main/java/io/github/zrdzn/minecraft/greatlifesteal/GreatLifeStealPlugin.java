package io.github.zrdzn.minecraft.greatlifesteal;

import io.github.zrdzn.minecraft.greatlifesteal.storage.Storage;
import io.github.zrdzn.minecraft.greatlifesteal.storage.StorageType;
import io.github.zrdzn.minecraft.greatlifesteal.storage.SqliteStorage;
import io.github.zrdzn.minecraft.greatlifesteal.user.UserListener;
import io.github.zrdzn.minecraft.greatlifesteal.repository.repositories.sqlite.SqliteUserRepository;
import io.github.zrdzn.minecraft.greatlifesteal.user.UserService;
import org.bukkit.Server;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreatLifeStealPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Logger logger = LoggerFactory.getLogger("GreatLifeSteal");

        Configuration configuration = this.getConfig();

        Server server = this.getServer();

        PluginManager pluginManager = server.getPluginManager();

        Storage storage = null;
        UserService userService = null;

        StorageType type = StorageType.valueOf(configuration.getString("dataSource.type", "SQLITE").toUpperCase());
        if (type == StorageType.SQLITE) {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException exception) {
                logger.error("Could not find a driver for the sqlite data source.");
                pluginManager.disablePlugin(this);

                return;
            }

            storage = new SqliteStorage(logger, this);
            storage.parse(configuration.getConfigurationSection("dataSource"));

            userService = new UserService(new SqliteUserRepository(logger, (SqliteStorage) storage));
        }

        if (storage == null) {
            logger.error("Data source cannot be null.");
            pluginManager.disablePlugin(this);

            return;
        }

        storage.applySchemas();

        userService.load();

        int healthChange = configuration.getInt("baseSettings.healthChange", 2);
        UserListener userListener = new UserListener(userService, healthChange);

        pluginManager.registerEvents(userListener, this);
    }

}
