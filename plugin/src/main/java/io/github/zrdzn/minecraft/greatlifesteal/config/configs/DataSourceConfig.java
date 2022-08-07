package io.github.zrdzn.minecraft.greatlifesteal.config.configs;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.EnumProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.StringProperty;
import io.github.zrdzn.minecraft.greatlifesteal.storage.StorageType;

/**
 * Represents 'dataSource' section.
 */
public class DataSourceConfig implements SettingsHolder {

    @Comment("A type of the data source that should be used. Available: SQLITE")
    public static final Property<StorageType> TYPE = new EnumProperty<>(
            StorageType.class, "dataSource.type",
            StorageType.SQLITE
    );

    @Comment("A file name for the SQLite data source.")
    public static final Property<String> SQLITE_FILE = new StringProperty(
            "dataSource.sqliteFile",
            "gls.db"
    );

    private DataSourceConfig() {
    }

    @Override
    public void registerComments(CommentsConfiguration config) {
        config.setComment(
                "dataSource",
                "A section for the data source configuration."
        );
    }

}
