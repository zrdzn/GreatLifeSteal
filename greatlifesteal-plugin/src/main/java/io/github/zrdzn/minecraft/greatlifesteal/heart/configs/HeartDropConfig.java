package io.github.zrdzn.minecraft.greatlifesteal.heart.configs;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.BooleanProperty;
import ch.jalu.configme.properties.EnumProperty;
import ch.jalu.configme.properties.Property;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartDropLocation;

/**
 * Represents 'baseSettings.heartItem.drop' section.
 */
public class HeartDropConfig implements SettingsHolder {

    @Comment("If the heart item should be dropped on a maximum health limit exceed.")
    public static final Property<Boolean> ON_LIMIT_EXCEED = new BooleanProperty(
            "baseSettings.heartItem.drop.onLimitExceed",
            true
    );

    @Comment("If the heart item should be dropped on an every kill for a killer instead of increasing his maximum health automatically.")
    public static final Property<Boolean> ON_EVERY_KILL = new BooleanProperty(
            "baseSettings.heartItem.drop.onEveryKill",
            false
    );

    @Comment({
            "Where should heart item be dropped first?",
            "Available: INVENTORY, GROUND_LEVEL, EYE_LEVEL"
    })
    public static final Property<HeartDropLocation> LOCATION = new EnumProperty<>(
            HeartDropLocation.class, "baseSettings.heartItem.drop.location",
            HeartDropLocation.INVENTORY
    );

    @Comment({
            "Where should heart item be dropped if it did not fit into inventory?",
            "Choose NONE if you want to block giving it and show error instead.",
            "Available: NONE, GROUND_LEVEL, EYE_LEVEL"
    })
    public static final Property<HeartDropLocation> FULL_INVENTORY_LOCATION = new EnumProperty<>(
            HeartDropLocation.class, "baseSettings.heartItem.drop.fullInventoryLocation",
            HeartDropLocation.GROUND_LEVEL
    );

    private HeartDropConfig() {
    }

    @Override
    public void registerComments(CommentsConfiguration config) {
        config.setComment(
                "baseSettings.heartItem.drop",
                "Specify when the heart item should be dropped."
        );
    }

}
