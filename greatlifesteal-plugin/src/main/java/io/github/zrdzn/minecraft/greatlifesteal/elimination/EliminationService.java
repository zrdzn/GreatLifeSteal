package io.github.zrdzn.minecraft.greatlifesteal.elimination;

import panda.std.Blank;
import panda.std.Result;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class EliminationService {

    private final EliminationRepository repository;

    public EliminationService(EliminationRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<Result<Elimination, Exception>> createElimination(Elimination elimination) {
        return CompletableFuture.supplyAsync(() -> this.repository.save(elimination));
    }

    public CompletableFuture<Result<List<Elimination>, Exception>> getAllEliminations() {
        return CompletableFuture.supplyAsync(this.repository::listAll);
    }

    public CompletableFuture<Result<Optional<Elimination>, Exception>> getElimination(int id) {
        return CompletableFuture.supplyAsync(() -> this.repository.findById(id));
    }

    public CompletableFuture<Result<Optional<Elimination>, Exception>> getElimination(UUID playerUuid) {
        return CompletableFuture.supplyAsync(() -> this.repository.findByPlayerUuid(playerUuid));
    }

    public CompletableFuture<Result<Blank, Exception>> removeElimination(int id) {
        return CompletableFuture.supplyAsync(() -> this.repository.deleteById(id));
    }

    public CompletableFuture<Result<Blank, Exception>> removeElimination(UUID playerUuid) {
        return CompletableFuture.supplyAsync(() -> this.repository.deleteByPlayerUuid(playerUuid));
    }

}
