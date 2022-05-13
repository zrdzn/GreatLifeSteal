package io.github.zrdzn.minecraft.greatlifesteal;

import io.github.zrdzn.minecraft.greatlifesteal.datasource.DataSource;
import io.github.zrdzn.minecraft.greatlifesteal.datasource.DataSourceType;
import io.github.zrdzn.minecraft.greatlifesteal.datasource.SqliteDataSource;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreatLifeStealPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Logger logger = LoggerFactory.getLogger("GreatLifeSteal");

        Configuration configuration = this.getConfig();

        DataSourceType type = DataSourceType.valueOf(configuration.getString("dataSource.type", "SQLITE"));
        if (type == DataSourceType.SQLITE) {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException exception) {
                logger.error("Could not find a driver for the sqlite data source.");
                this.getServer().getPluginManager().disablePlugin(this);

                return;
            }

            DataSource dataSource = new SqliteDataSource(logger);
        }
    }

}
