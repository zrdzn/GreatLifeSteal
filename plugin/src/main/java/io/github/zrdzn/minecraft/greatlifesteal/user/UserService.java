package io.github.zrdzn.minecraft.greatlifesteal.user;

import io.github.zrdzn.minecraft.greatlifesteal.repository.UserRepository;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserService {

    private final UserRepository repository;

    private Map<UUID, Integer> users;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public void load() {
        this.users = this.repository.listAll();
    }

    public CompletableFuture<Boolean> createUser(UUID userUuid, int health) {
        return CompletableFuture.supplyAsync(() -> {
            if (this.users.containsKey(userUuid)) {
                return false;
            }

            if (!this.repository.save(userUuid, health)) {
                return false;
            }

            this.users.put(userUuid, health);
            return true;
        });
    }

    public CompletableFuture<Optional<Entry<UUID, Integer>>> getUser(UUID userUuid) {
        return CompletableFuture.supplyAsync(() -> {
            Integer health = this.users.get(userUuid);
            if (health == null) {
                return Optional.of(new SimpleImmutableEntry<>(userUuid, health));
            }

            return this.repository.findByUserId(userUuid);
        });
    }

    public CompletableFuture<Boolean> setHealth(UUID userUuid, int health) {
        return CompletableFuture.supplyAsync(() -> {
            this.users.put(userUuid, health);
            return this.repository.setHealthByUserId(userUuid, health);
        });
    }

    public CompletableFuture<Boolean> changeHealth(UUID userUuid, int change) {
        return CompletableFuture.supplyAsync(() -> {
            Integer health = this.users.get(userUuid);
            if (health == null) {
                this.users.put(userUuid, change);
                return false;
            }

            return this.repository.changeHealthByUserId(userUuid, change);
        });
    }

    public CompletableFuture<Boolean> removeUser(UUID userUuid) {
        return CompletableFuture.supplyAsync(() -> {
            this.users.remove(userUuid);
            return this.repository.deleteByUserId(userUuid);
        });
    }

}
