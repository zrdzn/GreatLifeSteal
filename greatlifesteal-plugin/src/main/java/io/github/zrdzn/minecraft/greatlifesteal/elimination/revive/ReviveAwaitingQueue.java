package io.github.zrdzn.minecraft.greatlifesteal.elimination.revive;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReviveAwaitingQueue {

    private final Map<UUID, String> playersWaitingForRevive = new HashMap<>();

    public void addPlayer(UUID playerUuid, String reviveKey) {
        this.playersWaitingForRevive.put(playerUuid, reviveKey);
    }

    public boolean isPlayerPresent(UUID playerUuid) {
        return this.playersWaitingForRevive.containsKey(playerUuid);
    }

    public String getReviveKey(UUID playerUuid) {
        return this.playersWaitingForRevive.get(playerUuid);
    }

    public void removePlayer(UUID playerUuid) {
        this.playersWaitingForRevive.remove(playerUuid);
    }

}
