package io.github.zrdzn.minecraft.greatlifesteal.config;

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
            "&a&lAvailable commands:\n" +
                    "&7/&alifesteal reload &8- &eReloads the plugin.\n" +
                    "&7/&alifesteal lives <action> [player] &8- &eShows the health points for the player.\n" +
                    "&7/&alifesteal withdraw <hearts> [player] &8- &eWithdraws hearts as items." +
                    "&7/&alifesteal health add <player> <health_points> &8- &eAdds the health points to the player.\n" +
                    "&7/&alifesteal health remove <player> <health_points> &8- &eSubtracts the health points from the player.\n" +
                    "&7/&alifesteal health set <player> <health_points> &8- &eSets the health points for the player.\n" +
                    "&7/&alifesteal eliminate <action> <player> &8- &eEliminates the player through the specified action.\n" +
                    "&7/&alifesteal revive <action> <player> &8- &eRevives the player for the specified action."
    );

    public static final Property<String> NO_PERMISSIONS = new StringProperty(
            "messages.noPermissions",
            "&cYou don't have enough permissions."
    );

    public static final Property<String> SUCCESSFUL_COMMAND_ADD = new StringProperty(
            "messages.successfulCommandAdd",
            "&aYou have successfully added &e{HEALTH} &ahp to &e{PLAYER}&a."
    );

    public static final Property<String> SUCCESSFUL_COMMAND_REMOVE = new StringProperty(
            "messages.successfulCommandRemove",
            "&aYou have successfully subtracted &e{HEALTH} &ahp from &e{PLAYER}&a."
    );

    public static final Property<String> SUCCESSFUL_COMMAND_SET = new StringProperty(
            "messages.successfulCommandSet",
            "&aYou have successfully set &e{HEALTH} &ahp for &e{PLAYER}&a."
    );

    public static final Property<String> SUCCESSFUL_COMMAND_LIVES = new StringProperty(
            "messages.successfulCommandLives",
            "&aPlayer &e{PLAYER} &ahas &e{LIVES} lives &aleft."
    );

    public static final Property<String> SUCCESSFUL_COMMAND_WITHDRAW = new StringProperty(
            "messages.successfulCommandWithdraw",
            "&aPlayer &e{PLAYER} &ahas been given &e{HEARTS}&ax hearts."
    );

    public static final Property<String> NOT_ENOUGH_HEALTH_WITHDRAW = new StringProperty(
            "messages.notEnoughHealthWithdraw",
            "&cYou cannot withdraw more hearts than you already have."
    );

    public static final Property<String> NOT_ENOUGH_PLACE_INVENTORY = new StringProperty(
            "messages.notEnoughPlaceInventory",
            "&cYou do not have enough place in the inventory."
    );

    public static final Property<String> NO_ACTION_ENABLED = new StringProperty(
            "messages.noActionEnabled",
            "&cEnable at least one action in the configuration."
    );

    public static final Property<String> ACTION_TYPE_INVALID = new StringProperty(
            "messages.actionTypeInvalid",
            "&cSpecified action does not match the requirements."
    );

    public static final Property<String> NO_ACTION_SPECIFIED = new StringProperty(
            "messages.noActionSpecified",
            "&cYou need to specify a type of an action."
    );

    public static final Property<String> ELIMINATION_PRESENT = new StringProperty(
            "messages.eliminationPresent",
            "&cPlayer {PLAYER} is already eliminated."
    );

    public static final Property<String> NO_ELIMINATION_PRESENT = new StringProperty(
            "messages.noEliminationPresent",
            "&cPlayer {PLAYER} is not eliminated."
    );

    public static final Property<String> SUCCESSFUL_COMMAND_RELOAD = new StringProperty(
            "messages.successfulCommandReload",
            "&aPlugin has been successfully reloaded."
    );

    public static final Property<String> FAIL_COMMAND_RELOAD = new StringProperty(
            "messages.failCommandReload",
            "&cCould not reload the plugin."
    );

    public static final Property<String> FAIL_COMMAND_ELIMINATE = new StringProperty(
            "messages.failCommandEliminate",
            "&cCould not create the elimination."
    );

    public static final Property<String> SUCCESS_DEFAULT_HEALTH_SET = new StringProperty(
            "messages.successDefaultHealthSet",
            "&aYou have been successfully revived."
    );

    public static final Property<String> FAIL_DEFAULT_HEALTH_SET = new StringProperty(
            "messages.failDefaultHealthSet",
            "&cCould not set a default maximum health for you."
    );

    public static final Property<String> INVALID_PLAYER_PROVIDED = new StringProperty(
            "messages.invalidPlayerProvided",
            "&cYou have provided invalid player."
    );

    public static final Property<String> INVALID_HEALTH_PROVIDED = new StringProperty(
            "messages.invalidHealthProvided",
            "&cYou have provided invalid health number."
    );

    public static final Property<String> NO_NUMBER_SPECIFIED = new StringProperty(
            "messages.noNumberSpecified",
            "&cYou need to specify a number."
    );

    public static final Property<String> INVALID_NUMBER_PROVIDED = new StringProperty(
            "messages.invalidNumberProvided",
            "&cYou have provided an invalid number."
    );

    public static final Property<String> POSITIVE_NUMBER_REQUIRED = new StringProperty(
            "messages.positiveNumberRequired",
            "&cYou need to provide a number higher than 0."
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
