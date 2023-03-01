package io.github.zrdzn.minecraft.greatlifesteal.config.configs.heart;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.BooleanProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.StringListProperty;
import ch.jalu.configme.properties.StringProperty;
import java.util.List;

/**
 * Represents 'baseSettings.heartItem.meta' section.
 */
public class HeartMetaConfig implements SettingsHolder {

    @Comment("Display name for the item.")
    public static final Property<String> DISPLAY_NAME = new StringProperty(
            "baseSettings.heartItem.meta.displayName",
            "&aThe Heart of an Elk"
    );

    @Comment("Lore for the item.")
    public static final Property<List<String>> LORE = new StringListProperty(
            "baseSettings.heartItem.meta.lore",
            "&aUse this item to give yourself health points."
    );

    @Comment("Should heart item glow (Enchantment effect)?")
    public static final Property<Boolean> GLOWING = new BooleanProperty(
            "baseSettings.heartItem.meta.glowing",
            false
    );

    private HeartMetaConfig() {
    }

    @Override
    public void registerComments(CommentsConfiguration config) {
        config.setComment(
                "baseSettings.heartItem.meta",
                "Meta for the heart item."
        );
    }

}
