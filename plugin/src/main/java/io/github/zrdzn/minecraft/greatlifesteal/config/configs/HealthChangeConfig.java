package io.github.zrdzn.minecraft.greatlifesteal.config.configs;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.IntegerProperty;
import ch.jalu.configme.properties.Property;

/**
 * Represents 'baseSettings.healthChange' section.
 */
public class HealthChangeConfig implements SettingsHolder {

    public static final Property<Integer> VICTIM = new IntegerProperty(
            "baseSettings.healthChange.victim",
            2
    );

    public static final Property<Integer> KILLER = new IntegerProperty(
            "baseSettings.healthChange.killer",
            2
    );

    @Override
    public void registerComments(CommentsConfiguration config) {
        config.setComment(
                "baseSettings.healthChange",
                "Amount of health points revoked when a player was killed or awarded when a kill was scored."
        );
    }
}
