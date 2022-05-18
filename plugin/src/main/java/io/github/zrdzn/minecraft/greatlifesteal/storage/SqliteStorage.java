package io.github.zrdzn.minecraft.greatlifesteal.storage;

import com.google.common.io.Files;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqliteStorage implements Storage {

    private final Logger logger;
    private final JavaPlugin plugin;
    private final PluginManager pluginManager;

    private String jdbc;

    public SqliteStorage(Logger logger, JavaPlugin plugin) {
        this.logger = logger;
        this.plugin = plugin;
        this.pluginManager = plugin.getServer().getPluginManager();
    }

    @Override
    public void parse(ConfigurationSection section) {
        File sqliteFile = new File(this.plugin.getDataFolder(), section.getString("sqliteFile", "gls.db"));
        if (!sqliteFile.exists()) {
            try {
                Files.createParentDirs(sqliteFile);

                if (!sqliteFile.createNewFile()) {
                    this.logger.error("Something went wrong while creating a new SQLite file.");
                    return;
                }
            } catch (final IOException exception) {
                this.logger.error("Something went wrong while creating a new SQLite file.");
                return;
            }
        }

        this.jdbc = "jdbc:sqlite:" + sqliteFile.getAbsolutePath();
    }

    @Override
    public StorageType getType() {
        return StorageType.SQLITE;
    }

    @Override
    public void applySchemas() {
        String query = "" +
            "CREATE TABLE IF NOT EXISTS gls_users (" +
            "   id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "   user_uuid VARCHAR(36) NOT NULL UNIQUE," +
            "   user_health INTEGER DEFAULT 0" +
            ");";

        try (
            Connection connection = this.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.executeUpdate();
        } catch (SQLException exception) {
            this.logger.error("Could not create the default SQLite scheme.", exception);
            this.pluginManager.disablePlugin(this.plugin);
        }
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(this.jdbc);
        } catch (SQLException exception) {
            this.logger.error("Could not get a connection for the SQLite data source.", exception);
            this.pluginManager.disablePlugin(this.plugin);
            return null;
        }
    }

}
