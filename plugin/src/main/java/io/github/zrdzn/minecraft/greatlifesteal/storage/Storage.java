package io.github.zrdzn.minecraft.greatlifesteal.storage;

import org.bukkit.configuration.ConfigurationSection;

public interface Storage {

    /**
     * Parse a data source from a configuration section.
     * Could be used for setting fields that are required
     * to create a connection to a data source.
     *
     * @param section a bukkit configuration section
     *
     */
    void parse(ConfigurationSection section);

    /**
     * Gets a type for a storage.
     *
     * @return the type for the storage
     */
    StorageType getType();

    /**
     * Creates schemas from strings or files.
     */
    void applySchemas();

}
