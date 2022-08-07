package io.github.zrdzn.minecraft.greatlifesteal.elimination;

import java.util.List;
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
    Result<Elimination, Exception> save(Elimination elimination);

    /**
     * Lists all existing records from the database.
     *
     * @return all existing records with an optional exception
     */
    Result<List<Elimination>, Exception> listAll();

    /**
     * Finds a record with the specified id.
     *
     * @param id the id of a record
     * @return an optional elimination with an optional exception
     */
    Result<Optional<Elimination>, Exception> findById(int id);

    /**
     * Finds a record with the specified player's uuid.
     *
     * @param playerUuid the uuid of a player
     * @return an optional elimination with an optional exception
     */
    Result<Optional<Elimination>, Exception> findByPlayerUuid(UUID playerUuid);

    /**
     * Deletes a record with the specified id.
     *
     * @param id the id of a record
     * @return an optional exception
     */
    Result<Blank, Exception> deleteById(int id);

    /**
     * Deletes a record with the specified player's uuid.
     *
     * @param playerUuid the uuid of a player
     * @return an optional exception
     */
    Result<Blank, Exception> deleteByPlayerUuid(UUID playerUuid);

}
