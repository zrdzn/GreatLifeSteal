package io.github.zrdzn.minecraft.greatlifesteal.elimination;

import java.time.Instant;
import java.util.UUID;

public class Elimination {

    private int id;
    private Instant createdAt;
    private UUID playerUuid;
    private String playerName;
    private String action;
    private EliminationReviveStatus revive;
    private String lastWorld;

    public Elimination() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getPlayerUuid() {
        return this.playerUuid;
    }

    public void setPlayerUuid(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public EliminationReviveStatus getRevive() {
        return this.revive;
    }

    public void setRevive(EliminationReviveStatus revive) {
        this.revive = revive;
    }

    public String getLastWorld() {
        return this.lastWorld;
    }

    public void setLastWorld(String lastWorld) {
        this.lastWorld = lastWorld;
    }

}
