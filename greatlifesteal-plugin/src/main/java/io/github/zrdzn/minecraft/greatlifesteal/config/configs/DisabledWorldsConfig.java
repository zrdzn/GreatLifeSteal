package io.github.zrdzn.minecraft.greatlifesteal.config.configs;

import java.util.List;
import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.StringListProperty;

/**
 * Represents 'baseSettings.disabledWorlds' section.
 */
public class DisabledWorldsConfig implements SettingsHolder {

    @Comment("Specify in what worlds should gaining/losing health be disabled.")
    public static final Property<List<String>> HEALTH_CHANGE = new StringListProperty(
            "baseSettings.disabledWorlds.healthChange",
            "custom_world"
    );

    @Comment({
            "Specify in what worlds should eliminations be disabled.",
            "Example: Eliminated player X from world Y can access world Z if it is listed here."
    })
    public static final Property<List<String>> ELIMINATIONS = new StringListProperty(
            "baseSettings.disabledWorlds.eliminations",
            "custom_world_2"
    );

    @Override
    public void registerComments(CommentsConfiguration config) {
        config.setComment(
                "baseSettings.disabledWorlds",
                "Specify in what worlds should each system be disabled or where it will not affect anything."
        );
    }

}
