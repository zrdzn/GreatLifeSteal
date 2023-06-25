package io.github.zrdzn.minecraft.greatlifesteal.elimination;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import panda.std.Blank;
import panda.std.Result;

public class EliminationFacade {

    private final EliminationRepository repository;

    public EliminationFacade(EliminationRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<Result<Elimination, Exception>> createElimination(Elimination elimination) {
        return CompletableFuture.supplyAsync(() -> this.repository.saveElimination(elimination));
    }

    public CompletableFuture<Result<Optional<Elimination>, Exception>> getElimination(UUID playerUuid) {
        return CompletableFuture.supplyAsync(() -> this.repository.findEliminationByPlayerUuid(playerUuid));
    }

    public CompletableFuture<Result<Optional<Elimination>, Exception>> getElimination(String playerName) {
        return CompletableFuture.supplyAsync(() -> this.repository.findEliminationByPlayerName(playerName));
    }

    public CompletableFuture<Result<Boolean, Exception>> changeReviveStatus(String playerName,
                                                                            EliminationReviveStatus status) {
        return CompletableFuture.supplyAsync(() -> this.repository.updateReviveByPlayerName(playerName, status));
    }

    public CompletableFuture<Result<Blank, Exception>> removeElimination(UUID playerUuid) {
        return CompletableFuture.supplyAsync(() -> this.repository.deleteEliminationByPlayerUuid(playerUuid));
    }

}
