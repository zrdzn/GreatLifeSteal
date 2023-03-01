package io.github.zrdzn.minecraft.greatlifesteal.elimination;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EliminationRemovalCache {

    private final List<UUID> playersWaitingForEliminationRemoval = new ArrayList<>();

    public void addPlayer(UUID playerUuid) {
        this.playersWaitingForEliminationRemoval.add(playerUuid);
    }

    public boolean isPlayerPresent(UUID playerUuid) {
        return this.playersWaitingForEliminationRemoval.contains(playerUuid);
    }

    public void removePlayer(UUID playerUuid) {
        this.playersWaitingForEliminationRemoval.remove(playerUuid);
    }

}
