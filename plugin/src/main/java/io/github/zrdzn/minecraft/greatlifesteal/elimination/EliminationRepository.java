package io.github.zrdzn.minecraft.greatlifesteal.elimination;

import panda.std.Blank;
import panda.std.Result;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EliminationRepository {

    Result<Elimination, Exception> save(Elimination elimination);

    Result<List<Elimination>, Exception> listAll();

    Result<Optional<Elimination>, Exception> findById(int id);

    Result<Optional<Elimination>, Exception> findByPlayerUuid(UUID playerUuid);

    Result<Blank, Exception> deleteById(int id);

    Result<Blank, Exception> deleteByPlayerUuid(UUID playerUuid);

}
