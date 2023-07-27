package io.github.zrdzn.minecraft.greatlifesteal.storage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import ch.jalu.configme.SettingsManager;
import com.google.common.io.Files;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageFactory {

    private final Logger logger = LoggerFactory.getLogger(StorageFactory.class);

    private final SettingsManager config;
    private final Plugin plugin;

    public StorageFactory(SettingsManager config, Plugin plugin) {
        this.config = config;
        this.plugin = plugin;
    }

    public Storage createStorage() {
        HikariDataSource dataSource;
        StorageType storageType;

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setUsername(this.config.getProperty(StorageConfig.USER));
        hikariConfig.setPassword(this.config.getProperty(StorageConfig.PASSWORD));
        hikariConfig.addDataSourceProperty("cachePrepStmts", true);
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", 250);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        hikariConfig.addDataSourceProperty("useServerPrepStmts", true);
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", true);
        hikariConfig.addDataSourceProperty("tcpKeepAlive", true);
        hikariConfig.setLeakDetectionThreshold(60000L);
        hikariConfig.setMaximumPoolSize(this.config.getProperty(StorageConfig.MAXIMUM_POOL_SIZE));
        hikariConfig.setConnectionTimeout(this.config.getProperty(StorageConfig.CONNECTION_TIMEOUT));
        hikariConfig.setMinimumIdle(0);
        hikariConfig.setIdleTimeout(30000L);

        String host = this.config.getProperty(StorageConfig.HOST);
        int port = this.config.getProperty(StorageConfig.PORT);
        String database = this.config.getProperty(StorageConfig.DATABASE);

        switch (this.config.getProperty(StorageConfig.TYPE)) {
            case MYSQL:
                boolean enableSsl = this.config.getProperty(StorageConfig.ENABLE_SSL);
                if (!enableSsl) {
                    this.logger.warn("Storage connection is configured without SSL enabled.");
                }

                hikariConfig.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s?ssl=%s", host, port, database, enableSsl));

                try {
                    dataSource = new HikariDataSource(hikariConfig);
                    this.logger.info("Choosing MySQL as a storage provider.");
                } catch (Exception exception) {
                    throw new StorageException("Could not open connection to MySQL database.", exception);
                }

                String mysqlCreateEliminationTableQuery =
                        "CREATE TABLE IF NOT EXISTS gls_eliminations (" +
                                "   id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                                "   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_DATE," +
                                "   player_uuid VARCHAR(36) NOT NULL UNIQUE," +
                                "   player_name TEXT NOT NULL UNIQUE," +
                                "   action TEXT NOT NULL," +
                                "   revive TEXT NOT NULL DEFAULT 'PENDING'," +
                                "   last_world TEXT NOT NULL" +
                                ");";
                this.applySchema(dataSource, mysqlCreateEliminationTableQuery);

                storageType = StorageType.MYSQL;

                break;
            case SQLITE:
                try {
                    Class.forName("org.sqlite.JDBC");
                } catch (ClassNotFoundException exception) {
                    this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
                    throw new RuntimeException("SQLite driver not found.", exception);
                }

                File sqliteFile = new File(this.plugin.getDataFolder(), this.config.getProperty(StorageConfig.SQLITE_FILE));
                if (!sqliteFile.exists()) {
                    try {
                        Files.createParentDirs(sqliteFile);

                        sqliteFile.createNewFile();
                    } catch (IOException exception) {
                        throw new StorageException("Could not create SQLite file.", exception);
                    }
                }

                hikariConfig.setJdbcUrl("jdbc:sqlite:" + sqliteFile.getAbsolutePath());

                try {
                    dataSource = new HikariDataSource(hikariConfig);
                    this.logger.info("Choosing SQLite as a storage provider.");
                } catch (Exception exception) {
                    throw new StorageException("Could not open connection to SQLite file.", exception);
                }

                String sqliteCreateEliminationTableQuery =
                        "CREATE TABLE IF NOT EXISTS gls_eliminations (" +
                                "   id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                                "   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                                "   player_uuid VARCHAR(36) NOT NULL UNIQUE," +
                                "   player_name VARCHAR NOT NULL UNIQUE," +
                                "   action VARCHAR NOT NULL," +
                                "   revive VARCHAR NOT NULL DEFAULT 'PENDING'," +
                                "   last_world VARCHAR NOT NULL" +
                                ");";
                this.applySchema(dataSource, sqliteCreateEliminationTableQuery);

                storageType = StorageType.SQLITE;

                break;
            default:
                throw new IllegalArgumentException("There is no such storage type.");
        }

        return new Storage(dataSource, storageType);
    }

    private void applySchema(DataSource dataSource, String schemaCreateQuery) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(schemaCreateQuery)) {
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new StorageException("Could not create table.", exception);
        }
    }

}
