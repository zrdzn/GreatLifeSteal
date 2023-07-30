package io.github.zrdzn.minecraft.greatlifesteal.elimination;

import java.util.Optional;
import java.util.UUID;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.revive.ReviveStatus;

public interface EliminationRepository {

    /**
     * Creates a new elimination.
     *
     * @param playerUuid the unique id of a player
     * @param playerName the name of a player
     * @param action the action that is a cause of elimination
     * @param lastWorld the last world where the player was
     *
     * @return an elimination
     *
     * @throws EliminationException if a repository access error occurs
     */
    Elimination createElimination(UUID playerUuid, String playerName, String action, String lastWorld);

    /**
     * Finds an elimination with the specified player's unique id.
     *
     * @param playerUuid the unique id of a player
     *
     * @return an optional elimination
     *
     * @throws EliminationException if a repository access error occurs
     */
    Optional<Elimination> findEliminationByPlayerUuid(UUID playerUuid);

    /**
     * Finds an elimination with the specified player's name.
     *
     * @param playerName the name of a player
     *
     * @return an optional elimination
     *
     * @throws EliminationException if a repository access error occurs
     */
    Optional<Elimination> findEliminationByPlayerName(String playerName);

    /**
     * Updates a revive status with the specified player's name
     *
     * @param playerName the name of a player
     *
     * @return true if the elimination was updated, false otherwise
     *
     * @throws EliminationException if a repository access error occurs
     */
    boolean updateReviveByPlayerName(String playerName, ReviveStatus status);

    /**
     * Deletes an elimination with the specified player's unique id.
     *
     * @param playerUuid the unique id of a player
     *
     * @throws EliminationException if a repository access error occurs
     */
    void removeEliminationByPlayerUuid(UUID playerUuid);

}
