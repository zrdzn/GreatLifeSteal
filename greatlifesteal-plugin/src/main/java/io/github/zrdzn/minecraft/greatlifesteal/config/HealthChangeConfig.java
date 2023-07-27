package io.github.zrdzn.minecraft.greatlifesteal.config;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.DoubleProperty;
import ch.jalu.configme.properties.Property;

/**
 * Represents 'baseSettings.healthChange' section.
 */
public class HealthChangeConfig implements SettingsHolder {

    @Comment("How much health points should be taken from a victim.")
    public static final Property<Double> VICTIM = new DoubleProperty(
            "baseSettings.healthChange.victim",
            2.0D
    );

    @Comment("How much health points should be given to a killer.")
    public static final Property<Double> KILLER = new DoubleProperty(
            "baseSettings.healthChange.killer",
            2.0D
    );

    @Override
    public void registerComments(CommentsConfiguration config) {
        config.setComment(
                "baseSettings.healthChange",
                "Amount of health points revoked when a player was killed or awarded when a kill was scored.",
                "If you want to disable the life steal system for a killer or a victim just set their health change to 0."
        );
    }

}
