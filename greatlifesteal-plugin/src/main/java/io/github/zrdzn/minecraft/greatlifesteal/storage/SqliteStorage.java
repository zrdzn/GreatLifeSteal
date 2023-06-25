package io.github.zrdzn.minecraft.greatlifesteal.storage;

import ch.jalu.configme.SettingsManager;
import com.google.common.io.Files;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import panda.std.Result;

public class SqliteStorage implements Storage {

    private final File dataFolder;

    private String jdbc;

    public SqliteStorage(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    @Override
    public Result<? extends Storage, StorageLoadException> load(SettingsManager config) {
        return Result.supplyThrowing(() -> {
            File sqliteFile = new File(this.dataFolder, config.getProperty(StorageConfig.SQLITE_FILE));
            if (!sqliteFile.exists()) {
                Files.createParentDirs(sqliteFile);

                if (!sqliteFile.createNewFile()) {
                    return this;
                }
            }

            this.jdbc = "jdbc:sqlite:" + sqliteFile.getAbsolutePath();

            String query =
                    "CREATE TABLE IF NOT EXISTS gls_eliminations (" +
                    "   id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_DATE," +
                    "   player_uuid VARCHAR(36) NOT NULL UNIQUE," +
                    "   player_name VARCHAR NOT NULL UNIQUE," +
                    "   action VARCHAR NOT NULL," +
                    "   revive VARCHAR NOT NULL DEFAULT 'PENDING'," +
                    "   last_world VARCHAR NOT NULL" +
                    ");";
            try (Connection connection = this.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.executeUpdate();
            }

            return this;
        }).mapErr(error -> new StorageLoadException("Could not load properties or apply schemes for SQLite type.", error));
    }

    @Override
    public StorageType getType() {
        return StorageType.SQLITE;
    }

    @Override
    public void stop() {
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.jdbc);
    }

}
