package io.github.zrdzn.minecraft.greatlifesteal.user;

import org.bukkit.Bukkit;

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

    public CompletableFuture<Boolean> createUser(UUID userId, int health) {
        return CompletableFuture.supplyAsync(() -> {
            if (this.users.containsKey(userId)) {
                Bukkit.getLogger().warning("cache contains key");
                return false;
            }

            if (!this.repository.save(userId, health)) {
                Bukkit.getLogger().warning("could not save");
                return false;
            }

            this.users.put(userId, health);
            Bukkit.getLogger().warning("put in the cache");
            return true;
        });
    }

    public CompletableFuture<Optional<Entry<UUID, Integer>>> getUser(UUID userId) {
        return CompletableFuture.supplyAsync(() -> {
            Integer health = this.users.get(userId);
            if (health == null) {
                return Optional.of(new SimpleImmutableEntry<>(userId, health));
            }

            return this.repository.findByUserId(userId);
        });
    }

    public CompletableFuture<Void> setHealth(UUID userId, int health) {
        return CompletableFuture.runAsync(() -> {
            this.users.put(userId, health);
            this.repository.setHealthByUserId(userId, health);
        });
    }

    public CompletableFuture<Void> changeHealth(UUID userId, int change) {
        return CompletableFuture.runAsync(() -> {
            Integer health = this.users.get(userId);
            if (health == null) {
                this.users.put(userId, change);
                return;
            }

            this.repository.changeHealthByUserId(userId, change);
        });
    }

    public CompletableFuture<Boolean> removeUser(UUID userId) {
        return CompletableFuture.supplyAsync(() -> {
            this.users.remove(userId);
            return this.repository.deleteByUserId(userId);
        });
    }

}
