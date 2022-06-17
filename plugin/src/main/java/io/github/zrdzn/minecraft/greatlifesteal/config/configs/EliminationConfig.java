package io.github.zrdzn.minecraft.greatlifesteal.config.configs;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.BooleanProperty;
import ch.jalu.configme.properties.EnumProperty;
import ch.jalu.configme.properties.IntegerProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.StringListProperty;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationModeAction;

import java.util.List;

/**
 * Represents 'baseSettings.eliminationMode' section.
 */
public class EliminationConfig implements SettingsHolder {

    @Comment("If any action below should be executed.")
    public static final Property<Boolean> ENABLED = new BooleanProperty(
        "baseSettings.eliminationMode.enabled",
        false
    );

    @Comment("Amount of health points that are needed to execute an action.")
    public static final Property<Integer> REQUIRED_HEALTH = new IntegerProperty(
        "baseSettings.eliminationMode.requiredHealth",
        4
    );

    @Comment({ "Action that should be done on reaching health points goal.",
        "Available:", " SPECTATOR_MODE - change player's game mode to spectator.",
        " DISPATCH_COMMANDS - execute a list of commands as a console.",
        " BROADCAST - broadcast a message that is specified below." })
    public static final Property<EliminationModeAction> ACTION = new EnumProperty<>(
        EliminationModeAction.class, "baseSettings.eliminationMode.action",
        EliminationModeAction.SPECTATOR_MODE
    );

    @Comment({ "If using DISPATCH_COMMANDS action, specify commands to execute below.", "Placeholders:",
        " {killer} - represents killer username",
        " {victim} - represents victim username",
        " {killer_max_health} - represents killer's max health",
        " {victim_max_health} - represents victim's max health" })
    public static final Property<List<String>> COMMANDS = new StringListProperty(
        "baseSettings.eliminationMode.commands",
        "ban {victim}"
    );

    @Comment({ "Message that should be broadcasted on the elimination.",
        "Works only if the action is set to BROADCAST.", "Placeholders:",
        " {killer} - represents killer username",
        " {victim} - represents victim username",
        " {killer_max_health} - represents killer's max health",
        " {victim_max_health} - represents victim's max health" })
    public static final Property<List<String>> BROADCAST_MESSAGES = new StringListProperty(
        "baseSettings.eliminationMode.broadcastMessages",
        "&aPlayer &e{victim} ({victim_max_health} hp) &ahas been eliminated by &e{killer} ({killer_max_health} hp)&a."
    );

    private EliminationConfig() {
    }

    @Override
    public void registerComments(CommentsConfiguration config) {
        config.setComment(
            "baseSettings.eliminationMode",
            "Define what will happen if a player reaches specific amount of maximum health points."
        );
    }

}
