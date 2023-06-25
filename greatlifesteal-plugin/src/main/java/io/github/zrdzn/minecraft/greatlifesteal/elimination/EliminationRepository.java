package io.github.zrdzn.minecraft.greatlifesteal.elimination;

import java.util.Optional;
import java.util.UUID;
import panda.std.Blank;
import panda.std.Result;

public interface EliminationRepository {

    /**
     * Saves a new record in the database.
     *
     * @param elimination the elimination dto
     * @return the newly created record with an optional exception
     */
    Result<Elimination, Exception> saveElimination(Elimination elimination);

    /**
     * Finds a record with the specified player's uuid.
     *
     * @param playerUuid the uuid of a player
     * @return an optional elimination with an optional exception
     */
    Result<Optional<Elimination>, Exception> findEliminationByPlayerUuid(UUID playerUuid);

    /**
     * Finds a record with the specified player's name.
     *
     * @param playerName the name of a player
     * @return an optional elimination with an optional exception
     */
    Result<Optional<Elimination>, Exception> findEliminationByPlayerName(String playerName);

    /**
     * Updates a record with the specified player's name.
     *
     * @param playerName the name of a player
     * @return a boolean whether it affected any rows with an optional exception
     */
    Result<Boolean, Exception> updateReviveByPlayerName(String playerName, EliminationReviveStatus status);

    /**
     * Deletes a record with the specified player's uuid.
     *
     * @param playerUuid the uuid of a player
     * @return an optional exception
     */
    Result<Blank, Exception> deleteEliminationByPlayerUuid(UUID playerUuid);

}
