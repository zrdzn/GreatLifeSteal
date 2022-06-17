package io.github.zrdzn.minecraft.greatlifesteal.config.configs;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.StringProperty;

/**
 * Represents 'messages' section.
 */
public class MessagesConfig implements SettingsHolder {

    public static final Property<String> COMMAND_USAGE = new StringProperty(
        "messages.commandUsage",
        "&aType /lifesteal set/reload/lives [player] [health_points]"
    );

    public static final Property<String> NO_PERMISSIONS = new StringProperty(
        "messages.noPermissions",
        "&cYou don't have enough permissions."
    );

    public static final Property<String> SUCCESSFUL_COMMAND_SET = new StringProperty(
        "messages.successfulCommandSet",
        "&aYou have successfully set &e{HEALTH} &ahp for &e{PLAYER}&a."
    );

    public static final Property<String> SUCCESSFUL_COMMAND_LIVES = new StringProperty(
        "messages.successfulCommandLives",
        "&aPlayer &e{PLAYER} &ahas &e{LIVES} lives &aleft."
    );

    public static final Property<String> ELIMINATION_NOT_ENABLED = new StringProperty(
        "messages.eliminationNotEnabled",
        "&cEnable the &eeliminationMode &csetting in order to use this command."
    );

    public static final Property<String> SUCCESSFUL_COMMAND_RELOAD = new StringProperty(
        "messages.successfulCommandReload",
        "&aPlugin has been successfully reloaded."
    );

    public static final Property<String> FAIL_COMMAND_RELOAD = new StringProperty(
        "messages.failCommandReload",
        "&cCould not reload the plugin."
    );

    public static final Property<String> INVALID_PLAYER_PROVIDED = new StringProperty(
        "messages.invalidPlayerProvided",
        "&cYou have provided invalid player."
    );

    public static final Property<String> INVALID_HEALTH_PROVIDED = new StringProperty(
        "messages.invalidHealthProvided",
        "&cYou have provided invalid health number."
    );

    public static final Property<String> MAX_HEALTH_REACHED = new StringProperty(
        "messages.maxHealthReached",
        "&cYou have reached the maximum amount of health points."
    );

    public static final Property<String> STEAL_COOLDOWN_ACTIVE = new StringProperty(
        "messages.stealCooldownActive",
        "&cYou can't steal hearts from this player for another &e{AMOUNT} seconds&c."
    );

    public static final Property<String> PLUGIN_OUTDATED = new StringProperty(
        "messages.pluginOutdated",
        "&7(&eGLifeSteal&7) &aNew update came out! https://www.spigotmc.org/resources/greatlifesteal.102206/."
    );

    private MessagesConfig() {
    }

    @Override
    public void registerComments(CommentsConfiguration config) {
        config.setComment(
            "messages",
            "All messages that can be sent by the plugin to players.",
            "If you want to prevent from displaying a specific message, just replace the value with ''."
        );
    }

}
