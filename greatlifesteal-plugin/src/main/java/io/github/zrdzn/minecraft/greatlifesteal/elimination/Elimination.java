package io.github.zrdzn.minecraft.greatlifesteal.elimination;

import java.time.Instant;
import java.util.UUID;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.revive.ReviveStatus;

public class Elimination {

    private final int id;
    private final Instant createdAt;
    private final UUID playerUuid;
    private final String playerName;
    private final String eliminationKey;
    private final ReviveStatus revive;
    private final String lastWorld;

    public Elimination(int id, Instant createdAt, UUID playerUuid, String playerName, String eliminationKey,
                       ReviveStatus revive, String lastWorld) {
        this.id = id;
        this.createdAt = createdAt;
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.eliminationKey = eliminationKey;
        this.revive = revive;
        this.lastWorld = lastWorld;
    }

    public int getId() {
        return this.id;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public UUID getPlayerUuid() {
        return this.playerUuid;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public String getEliminationKey() {
        return this.eliminationKey;
    }

    public ReviveStatus getRevive() {
        return this.revive;
    }

    public String getLastWorld() {
        return this.lastWorld;
    }

}
