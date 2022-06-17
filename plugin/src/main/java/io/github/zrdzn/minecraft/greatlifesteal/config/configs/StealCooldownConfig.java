package io.github.zrdzn.minecraft.greatlifesteal.config.configs;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.BooleanProperty;
import ch.jalu.configme.properties.IntegerProperty;
import ch.jalu.configme.properties.Property;

/**
 * Represents 'baseSettings.stealCooldown' section.
 */
public class StealCooldownConfig implements SettingsHolder {

    @Comment("If the life steal cooldown should be enabled on the server.")
    public static final Property<Boolean> ENABLED = new BooleanProperty(
        "baseSettings.stealCooldown.enabled",
        true
    );

    @Comment("Cooldown time in seconds.")
    public static final Property<Integer> COOLDOWN = new IntegerProperty(
        "baseSettings.stealCooldown.cooldown",
        30
    );

    @Override
    public void registerComments(CommentsConfiguration config) {
        config.setComment(
            "baseSettings.stealCooldown",
            "Specify if life steal cooldown should be enabled and how long should it last.",
            "Killers will not be able to take hearts from victims unless a cooldown time expires."
        );
    }

}
