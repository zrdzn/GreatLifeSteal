package io.github.zrdzn.minecraft.greatlifesteal.elimination;

import io.github.zrdzn.minecraft.greatlifesteal.storage.Storage;
import io.github.zrdzn.minecraft.greatlifesteal.storage.MysqlStorage;
import io.github.zrdzn.minecraft.greatlifesteal.storage.SqliteStorage;

public class EliminationServiceFactory {

    public static EliminationService createEliminationService(Storage storage) {
        switch (storage.getType()) {
            case MYSQL:
                return new EliminationService(new MysqlEliminationRepository((MysqlStorage) storage));
            case SQLITE:
                return new EliminationService(new SqliteEliminationRepository((SqliteStorage) storage));
            default:
                throw new IllegalArgumentException("There is no such storage type.");
        }
    }

}
