package io.github.zrdzn.minecraft.greatlifesteal.elimination;

import io.github.zrdzn.minecraft.greatlifesteal.elimination.infra.MysqlEliminationRepository;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.infra.SqliteEliminationRepository;
import io.github.zrdzn.minecraft.greatlifesteal.storage.Storage;

public class EliminationFacadeFactory {

    private final Storage storage;

    public EliminationFacadeFactory(Storage storage) {
        this.storage = storage;
    }

    public EliminationFacade createEliminationFacade() {
        switch (this.storage.getType()) {
            case MYSQL:
                return new EliminationFacade(new MysqlEliminationRepository(this.storage.getDataSource()));
            case SQLITE:
                return new EliminationFacade(new SqliteEliminationRepository(this.storage.getDataSource()));
            default:
                throw new IllegalArgumentException("There is no such storage type.");
        }
    }

}
