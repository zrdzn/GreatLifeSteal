package io.github.zrdzn.minecraft.greatlifesteal.elimination;

import java.util.Optional;
import java.util.UUID;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.revive.ReviveStatus;

public class EliminationFacade {

    private final EliminationRepository repository;

    public EliminationFacade(EliminationRepository repository) {
        this.repository = repository;
    }

    public Elimination createElimination(UUID playerUuid, String playerName, String action, String lastWorld) {
        if (playerUuid == null) {
            throw new EliminationException("Player's unique id cannot be null.");
        }

        if (playerName == null) {
            throw new EliminationException("Player's name cannot be null.");
        }

        if (action == null) {
            throw new EliminationException("Action cannot be null.");
        }

        if (lastWorld == null) {
            throw new EliminationException("Last world cannot be null.");
        }

        return this.repository.createElimination(playerUuid, playerName, action, lastWorld);
    }

    public Optional<Elimination> findEliminationByPlayerUuid(UUID playerUuid) {
        if (playerUuid == null) {
            throw new EliminationException("Player's unique id cannot be null.");
        }

        return this.repository.findEliminationByPlayerUuid(playerUuid);
    }

    public Optional<Elimination> findEliminationByPlayerName(String playerName) {
        if (playerName == null) {
            throw new EliminationException("Player's name cannot be null.");
        }

        return this.repository.findEliminationByPlayerName(playerName);
    }

    public boolean updateReviveByPlayerName(String playerName, ReviveStatus status) {
        if (playerName == null) {
            throw new EliminationException("Player's name cannot be null.");
        }

        if (status == null) {
            throw new EliminationException("Revive status cannot be null.");
        }

        return this.repository.updateReviveByPlayerName(playerName, status);
    }

    public void removeEliminationByPlayerUuid(UUID playerUuid) {
        if (playerUuid == null) {
            throw new EliminationException("Player's unique id cannot be null.");
        }

        this.repository.removeEliminationByPlayerUuid(playerUuid);
    }

}
