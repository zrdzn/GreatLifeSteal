package io.github.zrdzn.minecraft.greatlifesteal.elimination;

import io.github.zrdzn.minecraft.greatlifesteal.elimination.infra.MysqlEliminationRepository;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.infra.SqliteEliminationRepository;
import io.github.zrdzn.minecraft.greatlifesteal.storage.MysqlStorage;
import io.github.zrdzn.minecraft.greatlifesteal.storage.SqliteStorage;
import io.github.zrdzn.minecraft.greatlifesteal.storage.Storage;

public class EliminationFacadeFactory {

    public static EliminationFacade createEliminationFacade(Storage storage) {
        switch (storage.getType()) {
            case MYSQL:
                return new EliminationFacade(new MysqlEliminationRepository((MysqlStorage) storage));
            case SQLITE:
                return new EliminationFacade(new SqliteEliminationRepository((SqliteStorage) storage));
            default:
                throw new IllegalArgumentException("There is no such storage type.");
        }
    }

}
