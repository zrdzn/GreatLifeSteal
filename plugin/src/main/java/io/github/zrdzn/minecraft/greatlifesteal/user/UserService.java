package io.github.zrdzn.minecraft.greatlifesteal.user;

import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<Boolean> createUser(UUID userId, int health) {
        return CompletableFuture.supplyAsync(() -> this.repository.save(userId, health));
    }

    public CompletableFuture<Optional<Entry<UUID, Integer>>> getUser(UUID userId) {
        return CompletableFuture.supplyAsync(() -> this.repository.findByUserId(userId));
    }

    public CompletableFuture<Void> setHealth(UUID userId, int health) {
        return CompletableFuture.runAsync(() -> this.repository.setHealthByUserId(userId, health));
    }

    public CompletableFuture<Void> changeHealth(UUID userId, int change) {
        return CompletableFuture.runAsync(() -> this.repository.changeHealthByUserId(userId, change));
    }

    public CompletableFuture<Boolean> removeUser(UUID userId) {
        return CompletableFuture.supplyAsync(() -> this.repository.deleteByUserId(userId));
    }

}
