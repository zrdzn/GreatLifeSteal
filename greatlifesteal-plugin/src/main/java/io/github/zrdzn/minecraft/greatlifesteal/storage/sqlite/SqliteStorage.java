package io.github.zrdzn.minecraft.greatlifesteal.storage.sqlite;

import ch.jalu.configme.SettingsManager;
import com.google.common.io.Files;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.DataSourceConfig;
import io.github.zrdzn.minecraft.greatlifesteal.storage.Storage;
import io.github.zrdzn.minecraft.greatlifesteal.storage.StorageType;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import panda.std.Blank;
import panda.std.Result;

public class SqliteStorage implements Storage {

    private final SettingsManager config;
    private final File dataFolder;

    private String jdbc;

    public SqliteStorage(SettingsManager config, File dataFolder) {
        this.config = config;
        this.dataFolder = dataFolder;
    }

    @Override
    public Result<Blank, Exception> init() {
        return Result.attempt(() -> {
            this.loadProperties();
            this.applySchemas();

            return Blank.BLANK;
        });
    }

    @Override
    public StorageType getType() {
        return StorageType.SQLITE;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.jdbc);
    }

    private void loadProperties() throws IOException {
        File sqliteFile = new File(this.dataFolder, this.config.getProperty(DataSourceConfig.SQLITE_FILE));
        if (!sqliteFile.exists()) {
            Files.createParentDirs(sqliteFile);

            if (!sqliteFile.createNewFile()) {
                return;
            }
        }

        this.jdbc = "jdbc:sqlite:" + sqliteFile.getAbsolutePath();
    }

    private void applySchemas() throws SQLException {
        String query = "" +
                "CREATE TABLE IF NOT EXISTS gls_eliminations (" +
                "   id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_DATE," +
                "   player_uuid VARCHAR(36) NOT NULL UNIQUE," +
                "   action VARCHAR NOT NULL" +
                ");";

        try (Connection connection = this.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        }
    }

}
