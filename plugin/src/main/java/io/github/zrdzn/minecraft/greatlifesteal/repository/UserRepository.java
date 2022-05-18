package io.github.zrdzn.minecraft.greatlifesteal.repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    boolean save(UUID userId, int health);

    Optional<Map.Entry<UUID, Integer>> findByUserId(UUID userId);

    Map<UUID, Integer> listAll();

    boolean setHealthByUserId(UUID userId, int value);

    boolean changeHealthByUserId(UUID userId, int change);

    boolean deleteByUserId(UUID userId);

}
