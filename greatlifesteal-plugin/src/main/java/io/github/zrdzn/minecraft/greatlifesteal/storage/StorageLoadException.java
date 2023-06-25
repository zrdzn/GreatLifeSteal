package io.github.zrdzn.minecraft.greatlifesteal.storage;

public class StorageLoadException extends RuntimeException {

    public StorageLoadException(String message, Throwable exception) {
        super("Could not load the storage: " + message, exception);
    }

}
