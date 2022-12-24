package io.github.zrdzn.minecraft.greatlifesteal.elimination;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import panda.std.Blank;
import panda.std.Result;

public class EliminationService {

    private final EliminationRepository repository;

    public EliminationService(EliminationRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<Result<Elimination, Exception>> createElimination(Elimination elimination) {
        return CompletableFuture.supplyAsync(() -> this.repository.saveElimination(elimination));
    }

    public CompletableFuture<Result<List<Elimination>, Exception>> getAllEliminations() {
        return CompletableFuture.supplyAsync(this.repository::listAllEliminations);
    }

    public CompletableFuture<Result<Optional<Elimination>, Exception>> getElimination(int id) {
        return CompletableFuture.supplyAsync(() -> this.repository.findEliminationById(id));
    }

    public CompletableFuture<Result<Optional<Elimination>, Exception>> getElimination(UUID playerUuid) {
        return CompletableFuture.supplyAsync(() -> this.repository.findEliminationByPlayerUuid(playerUuid));
    }

    public CompletableFuture<Result<Optional<Elimination>, Exception>> getElimination(String playerName) {
        return CompletableFuture.supplyAsync(() -> this.repository.findEliminationByPlayerName(playerName));
    }

    public CompletableFuture<Result<Boolean, Exception>> changeReviveStatus(int id, EliminationReviveStatus status) {
        return CompletableFuture.supplyAsync(() -> this.repository.updateReviveById(id, status));
    }

    public CompletableFuture<Result<Boolean, Exception>> changeReviveStatus(UUID playerUuid, EliminationReviveStatus status) {
        return CompletableFuture.supplyAsync(() -> this.repository.updateReviveByPlayerUuid(playerUuid, status));
    }

    public CompletableFuture<Result<Boolean, Exception>> changeReviveStatus(String playerName, EliminationReviveStatus status) {
        return CompletableFuture.supplyAsync(() -> this.repository.updateReviveByPlayerName(playerName, status));
    }

    public CompletableFuture<Result<Blank, Exception>> removeElimination(int id) {
        return CompletableFuture.supplyAsync(() -> this.repository.deleteEliminationById(id));
    }

    public CompletableFuture<Result<Blank, Exception>> removeElimination(UUID playerUuid) {
        return CompletableFuture.supplyAsync(() -> this.repository.deleteEliminationByPlayerUuid(playerUuid));
    }

    public CompletableFuture<Result<Blank, Exception>> removeElimination(String playerName) {
        return CompletableFuture.supplyAsync(() -> this.repository.deleteEliminationByPlayerName(playerName));
    }

}
