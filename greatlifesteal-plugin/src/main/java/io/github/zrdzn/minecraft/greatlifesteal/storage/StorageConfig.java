package io.github.zrdzn.minecraft.greatlifesteal.storage;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class StorageConfig extends OkaeriConfig {

    @Comment("A type of the data source that should be used. Available: SQLITE, MYSQL")
    private StorageType type = StorageType.SQLITE;

    @Comment("A file name if SQLite is used.")
    private String sqliteFile = "greatlifesteal.db";

    @Comment("Database host address.")
    private String host = "localhost";

    @Comment("Database port.")
    private int port = 3306;

    @Comment("Database name.")
    private String database = "greatlifesteal";

    @Comment("Database user name.")
    private String user = "minecraft_user";

    @Comment("Database password.")
    private String password = "tomatoes";

    @Comment("Should SSL be enabled for database?")
    private boolean enableSsl = false;

    @Comment("Database maximum pool size.")
    private int maximumPoolSize = 10;

    @Comment("Database connection timeout.")
    private int connectionTimeout = 5000;

    public StorageType getType() {
        return this.type;
    }

    public void setType(StorageType type) {
        this.type = type;
    }

    public String getSqliteFile() {
        return this.sqliteFile;
    }

    public void setSqliteFile(String sqliteFile) {
        this.sqliteFile = sqliteFile;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDatabase() {
        return this.database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnableSsl() {
        return this.enableSsl;
    }

    public void setEnableSsl(boolean enableSsl) {
        this.enableSsl = enableSsl;
    }

    public int getMaximumPoolSize() {
        return this.maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public int getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

}
