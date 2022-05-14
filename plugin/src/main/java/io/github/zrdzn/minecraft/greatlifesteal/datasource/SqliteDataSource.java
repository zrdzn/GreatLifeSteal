package io.github.zrdzn.minecraft.greatlifesteal.datasource;

import com.google.common.io.Files;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class SqliteDataSource implements DataSource {

    private final JavaPlugin plugin;
    private final Logger logger;

    private String jdbc;

    public SqliteDataSource(JavaPlugin plugin, Logger logger) {
        this.plugin = plugin;
        this.logger = logger;
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
    public Map<String, String> getTables() {
        String users = "CREATE TABLE IF NOT EXISTS gls_users (" +
            "   id INT PRIMARY KEY," +
            "   user_uuid VARCHAR(36) NOT NULL UNIQUE KEY," +
            "   user_health INT DEFAULT 0" +
            ");";

        return Collections.singletonMap("gls_users", users);
    }

    @Override
    public Optional<ResultSet> query(String query, Object... replacements) {
        Optional<Connection> connectionMaybe = this.getConnection();
        if (!connectionMaybe.isPresent()) {
            return Optional.empty();
        }

        Connection connection = connectionMaybe.get();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (int index = 0; index < replacements.length; index++) {
                statement.setObject(index + 1, replacements[index]);
            }

            ResultSet resultSet = statement.executeQuery();
            if (resultSet == null || !resultSet.next()) {
                return Optional.empty();
            }

            connection.close();
            statement.closeOnCompletion();

            return Optional.of(resultSet);
        } catch (SQLException exception) {
            this.logger.error("Something went wrong while executing the query.", exception);
            return Optional.empty();
        }
    }

    @Override
    public int update(String query, Object... replacements) {
        Optional<Connection> connectionMaybe = this.getConnection();
        if (!connectionMaybe.isPresent()) {
            return -1;
        }

        Connection connection = connectionMaybe.get();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (int index = 1; index < replacements.length; index++) {
                statement.setObject(index, replacements[index]);
            }

            int affectedRows = statement.executeUpdate();

            connection.close();
            statement.closeOnCompletion();

            return affectedRows;
        } catch (SQLException exception) {
            this.logger.error("Something went wrong while executing the query.", exception);
            return -1;
        }
    }

    public Optional<Connection> getConnection() {
        try {
            return Optional.of(DriverManager.getConnection(this.jdbc));
        } catch (SQLException exception) {
            this.logger.error("Could not get a connection for the SQLite data source.", exception);
            return Optional.empty();
        }
    }

}
