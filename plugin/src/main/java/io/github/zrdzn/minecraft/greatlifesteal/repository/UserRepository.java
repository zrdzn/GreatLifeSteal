package io.github.zrdzn.minecraft.greatlifesteal.repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    boolean save(UUID userUuid, int health);

    Optional<Map.Entry<UUID, Integer>> findByUserId(UUID userUuid);

    Map<UUID, Integer> listAll();

    boolean setHealthByUserId(UUID userUuid, int value);

    boolean changeHealthByUserId(UUID userUuid, int change);

    boolean deleteByUserId(UUID userUuid);

}
