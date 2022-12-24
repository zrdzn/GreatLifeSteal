package io.github.zrdzn.minecraft.greatlifesteal.storage;

public class StorageLoadException extends Exception {

    public StorageLoadException(String message, Throwable exception) {
        super("Something went wrong while loading a storage: " + message, exception);
    }

}
