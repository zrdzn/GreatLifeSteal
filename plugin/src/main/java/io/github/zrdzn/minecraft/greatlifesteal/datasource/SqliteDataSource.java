package io.github.zrdzn.minecraft.greatlifesteal.datasource;

import org.bukkit.configuration.ConfigurationSection;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class SqliteDataSource implements DataSource {

    private final Logger logger;

    private String jdbc;

    public SqliteDataSource(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void parse(ConfigurationSection section) {
        String file = section.getString("sqliteFile", "gls.db");
        this.jdbc = "jdbc:sqlite:" + file;
    }

    @Override
    public Optional<ResultSet> query(String query, Object... replacements) {
        Optional<Connection> connectionMaybe = this.getConnection();
        if (!connectionMaybe.isPresent()) {
            return Optional.empty();
        }

        Connection connection = connectionMaybe.get();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            if (replacements.length > 0) {
                for (int index = 1; index < replacements.length; index++) {
                    statement.setObject(index, replacements[index]);
                }
            }

            ResultSet resultSet = statement.executeQuery();
            if (resultSet == null || !resultSet.next()) {
                return Optional.ofNullable(resultSet);
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
            if (replacements.length > 0) {
                for (int index = 1; index < replacements.length; index++) {
                    statement.setObject(index, replacements[index]);
                }
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

    private Optional<Connection> getConnection() {
        try {
            return Optional.of(DriverManager.getConnection(this.jdbc));
        } catch (SQLException exception) {
            this.logger.error("Could not get a connection for the SQLite data source.", exception);
            return Optional.empty();
        }
    }

}
