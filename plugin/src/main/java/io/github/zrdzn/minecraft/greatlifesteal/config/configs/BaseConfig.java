package io.github.zrdzn.minecraft.greatlifesteal.config.configs;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.BooleanProperty;
import ch.jalu.configme.properties.IntegerProperty;
import ch.jalu.configme.properties.Property;

/**
 * Represents 'baseSettings' section.
 */
public class BaseConfig implements SettingsHolder {

    @Comment("If health points should be decreased from a player who is killed.")
    public static final Property<Boolean> TAKE_HEALTH_FROM_VICTIM = new BooleanProperty(
            "baseSettings.takeHealthFromVictim",
            true
    );

    @Comment("If health points should be increased for a player who killed somebody.")
    public static final Property<Boolean> GIVE_HEALTH_TO_KILLER = new BooleanProperty(
            "baseSettings.giveHealthToKiller",
            true
    );

    @Comment("Amount of maximum health points that will be given to the new player that did not play before.")
    public static final Property<Integer> DEFAULT_HEALTH = new IntegerProperty(
            "baseSettings.defaultHealth",
            20
    );

    @Comment("Amount of health points revoked when a player was killed or awarded when a kill was scored.")
    public static final Property<Integer> HEALTH_CHANGE = new IntegerProperty(
            "baseSettings.healthChange",
            2
    );

    @Comment("Minimum amount of health points a player can have.")
    public static final Property<Integer> MINIMUM_HEALTH = new IntegerProperty(
            "baseSettings.minimumHealth",
            2
    );

    @Comment("Maximum amount of health points a player can have.")
    public static final Property<Integer> MAXIMUM_HEALTH = new IntegerProperty(
            "baseSettings.maximumHealth",
            40
    );

    @Comment("Health points will be changed only if the player was killed by the other player.")
    public static final Property<Boolean> KILL_BY_PLAYER_ONLY = new BooleanProperty(
            "baseSettings.killByPlayerOnly",
            true
    );

    @Comment({
            "If players with same ip address should be prevented from gaining health points.",
            "This option can prevent from farming health points via multi accounts on a single device."
    })
    public static final Property<Boolean> IGNORE_SAME_IP = new BooleanProperty(
            "baseSettings.ignoreSameIp",
            true
    );

    private BaseConfig() {
    }

    @Override
    public void registerComments(CommentsConfiguration config) {
        config.setComment(
                "baseSettings",
                "A section for the plugin operation.", "1 heart = 2 health points."
        );
    }

}
