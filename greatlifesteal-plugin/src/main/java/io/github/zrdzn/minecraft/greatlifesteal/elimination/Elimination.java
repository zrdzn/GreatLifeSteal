package io.github.zrdzn.minecraft.greatlifesteal.elimination;

import java.time.Instant;
import java.util.UUID;

public class Elimination {

    private final int id;
    private final Instant createdAt;
    private final UUID playerUuid;
    private final String playerName;
    private final String action;
    private final EliminationReviveStatus revive;
    private final String lastWorld;

    public Elimination(int id, Instant createdAt, UUID playerUuid, String playerName, String action,
                       EliminationReviveStatus revive, String lastWorld) {
        this.id = id;
        this.createdAt = createdAt;
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.action = action;
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

    public String getAction() {
        return this.action;
    }

    public EliminationReviveStatus getRevive() {
        return this.revive;
    }

    public String getLastWorld() {
        return this.lastWorld;
    }

}
