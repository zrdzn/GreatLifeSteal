package io.github.zrdzn.minecraft.greatlifesteal;

import io.github.zrdzn.minecraft.greatlifesteal.datasource.DataSource;
import io.github.zrdzn.minecraft.greatlifesteal.datasource.DataSourceType;
import io.github.zrdzn.minecraft.greatlifesteal.datasource.SqliteDataSource;
import io.github.zrdzn.minecraft.greatlifesteal.user.UserListener;
import io.github.zrdzn.minecraft.greatlifesteal.user.UserRepository;
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

        DataSource dataSource = null;

        DataSourceType type = DataSourceType.valueOf(configuration.getString("dataSource.type", "SQLITE").toUpperCase());
        if (type == DataSourceType.SQLITE) {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException exception) {
                logger.error("Could not find a driver for the sqlite data source.");
                pluginManager.disablePlugin(this);

                return;
            }

            dataSource = new SqliteDataSource(this, logger);
            dataSource.parse(configuration.getConfigurationSection("dataSource"));
        }

        if (dataSource == null) {
            logger.error("Data source cannot be null.");
            pluginManager.disablePlugin(this);

            return;
        }

        dataSource.getTables().values().forEach(dataSource::update);

        UserService userService = new UserService(new UserRepository(logger, dataSource));
        userService.load();

        int healthChange = configuration.getInt("baseSettings.healthChange", 2);
        UserListener userListener = new UserListener(this, userService, healthChange);

        pluginManager.registerEvents(userListener, this);
    }

}
