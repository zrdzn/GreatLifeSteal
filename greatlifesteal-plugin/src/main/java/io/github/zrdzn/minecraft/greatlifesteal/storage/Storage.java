package io.github.zrdzn.minecraft.greatlifesteal.storage;

import ch.jalu.configme.SettingsManager;
import panda.std.Result;

public interface Storage {


    /**
     * An entry point for a data source.
     * It executes in the first place, so it can have some
     * configurations, parsers, default schemas creations etc.
     *
     */
    Result<? extends Storage, StorageLoadException> load(SettingsManager config);

    /**
     * Gets a type for a storage.
     *
     * @return the type for the storage
     */
    StorageType getType();

    /**
     * Used to stop the data source or a connection behind it.
     */
    void stop();

}
