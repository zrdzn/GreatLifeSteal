package io.github.zrdzn.minecraft.greatlifesteal.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import ch.jalu.configme.SettingsManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import panda.std.Result;

public class MysqlStorage implements Storage, Poolable {

    private final Logger logger = LoggerFactory.getLogger(MysqlStorage.class);

    private HikariDataSource hikariDataSource;

    @Override
    public Result<? extends Storage, StorageLoadException> load(SettingsManager config) {
        HikariConfig hikariConfig = new HikariConfig();

        boolean enableSsl = config.getProperty(StorageConfig.ENABLE_SSL);
        if (!enableSsl) {
            this.logger.warn("Storage connection is configured without SSL enabled.");
        }

        String host = config.getProperty(StorageConfig.HOST);
        int port = config.getProperty(StorageConfig.PORT);
        String database = config.getProperty(StorageConfig.DATABASE);
        String user = config.getProperty(StorageConfig.USER);
        String password = config.getProperty(StorageConfig.PASSWORD);
        int maximumPoolSize = config.getProperty(StorageConfig.MAXIMUM_POOL_SIZE);
        int connectionTimeout = config.getProperty(StorageConfig.CONNECTION_TIMEOUT);

        hikariConfig.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s?ssl=%s", host, port, database, enableSsl));
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);
        hikariConfig.addDataSourceProperty("cachePrepStmts", true);
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", 250);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        hikariConfig.addDataSourceProperty("useServerPrepStmts", true);
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", true);
        hikariConfig.addDataSourceProperty("tcpKeepAlive", true);
        hikariConfig.setLeakDetectionThreshold(60000L);
        hikariConfig.setMaximumPoolSize(maximumPoolSize);
        hikariConfig.setConnectionTimeout(connectionTimeout);
        hikariConfig.setMinimumIdle(0);
        hikariConfig.setIdleTimeout(30000L);

        this.hikariDataSource = new HikariDataSource(hikariConfig);

        String createTable =
                "CREATE TABLE IF NOT EXISTS gls_eliminations (" +
                        "   id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                        "   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_DATE," +
                        "   player_uuid VARCHAR(36) NOT NULL UNIQUE," +
                        "   player_name TEXT NOT NULL UNIQUE," +
                        "   action TEXT NOT NULL," +
                        "   revive TEXT NOT NULL DEFAULT 'PENDING'," +
                        "   last_world TEXT NOT NULL" +
                        ");";
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.getHikariDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement(createTable)) {
                statement.executeUpdate();
            }

            return this;
        }).mapErr(error -> new StorageLoadException("Could not create the table.", error));
    }

    @Override
    public StorageType getType() {
        return StorageType.MYSQL;
    }

    @Override
    public void stop() {
        this.hikariDataSource.close();
    }

    @Override
    public HikariDataSource getHikariDataSource() {
        return this.hikariDataSource;
    }

}
