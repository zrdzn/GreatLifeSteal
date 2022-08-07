package io.github.zrdzn.minecraft.greatlifesteal.storage;

import panda.std.Blank;
import panda.std.Result;

public interface Storage {

    /**
     * An entry point for the data source.
     * It executes in the first place, so it can have some
     * configurations, parsers, default schemas creations etc.
     *
     * @return the blank result or an exception if an error occurred
     */
    Result<Blank, Exception> init();

    /**
     * Gets a type for a storage.
     *
     * @return the type for the storage
     */
    StorageType getType();

}
