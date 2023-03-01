package io.github.zrdzn.minecraft.greatlifesteal.storage;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.BooleanProperty;
import ch.jalu.configme.properties.EnumProperty;
import ch.jalu.configme.properties.IntegerProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.StringProperty;

/**
 * Represents 'dataSource' section.
 */
public class StorageConfig implements SettingsHolder {

    @Comment("A type of the data source that should be used. Available: SQLITE, MYSQL")
    public static final Property<StorageType> TYPE = new EnumProperty<>(
            StorageType.class, "dataSource.type",
            StorageType.SQLITE
    );

    @Comment("A file name if SQLite is used.")
    public static final Property<String> SQLITE_FILE = new StringProperty(
            "dataSource.sqliteFile",
            "gls.db"
    );

    @Comment("Database host address.")
    public static final Property<String> HOST = new StringProperty(
            "dataSource.host",
            "localhost"
    );

    @Comment("Database port.")
    public static final Property<Integer> PORT = new IntegerProperty(
            "dataSource.port",
            3306
    );

    @Comment("Database name.")
    public static final Property<String> DATABASE = new StringProperty(
            "dataSource.database",
            "gls"
    );

    @Comment("Database user name.")
    public static final Property<String> USER = new StringProperty(
            "dataSource.user",
            "root"
    );

    @Comment("Database password.")
    public static final Property<String> PASSWORD = new StringProperty(
            "dataSource.password",
            "password"
    );

    @Comment("Should SSL be enabled for database?")
    public static final Property<Boolean> ENABLE_SSL = new BooleanProperty(
            "dataSource.enableSsl",
            false
    );

    @Comment("Database maximum pool size.")
    public static final Property<Integer> MAXIMUM_POOL_SIZE = new IntegerProperty(
            "dataSource.maximumPoolSize",
            10
    );

    @Comment("Database connection timeout.")
    public static final Property<Integer> CONNECTION_TIMEOUT = new IntegerProperty(
            "dataSource.connectionTimeout",
            5000
    );


    private StorageConfig() {
    }

    @Override
    public void registerComments(CommentsConfiguration config) {
        config.setComment(
                "dataSource",
                "A section for the data source configuration."
        );
    }

}
