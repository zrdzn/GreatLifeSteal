package io.github.zrdzn.minecraft.greatlifesteal.heart;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class HeartDropConfig extends OkaeriConfig {

    @Comment("If the heart item is dropped when the maximum health limit is exceeded.")
    private boolean onLimitExceed = true;

    @Comment("If the heart item should be dropped with every kill for the killer instead of automatically increasing their maximum health.")
    private boolean onEveryKill = false;

    @Comment("Where should heart item be dropped first?")
    @Comment("Available: INVENTORY, GROUND_LEVEL, EYE_LEVEL")
    private HeartDropLocation location = HeartDropLocation.INVENTORY;

    @Comment("Where should the heart item be dropped if it does not fit into inventory?")
    @Comment("Choose NONE if you want to block giving it and show an error instead.")
    @Comment("Available: NONE, GROUND_LEVEL, EYE_LEVEL")
    private HeartDropLocation locationOnFullInventory = HeartDropLocation.GROUND_LEVEL;

    public boolean isOnLimitExceed() {
        return this.onLimitExceed;
    }

    public void setOnLimitExceed(boolean onLimitExceed) {
        this.onLimitExceed = onLimitExceed;
    }

    public boolean isOnEveryKill() {
        return this.onEveryKill;
    }

    public void setOnEveryKill(boolean onEveryKill) {
        this.onEveryKill = onEveryKill;
    }

    public HeartDropLocation getLocation() {
        return this.location;
    }

    public void setLocation(HeartDropLocation location) {
        this.location = location;
    }

    public HeartDropLocation getLocationOnFullInventory() {
        return this.locationOnFullInventory;
    }

    public void setLocationOnFullInventory(
            HeartDropLocation locationOnFullInventory) {
        this.locationOnFullInventory = locationOnFullInventory;
    }

}
