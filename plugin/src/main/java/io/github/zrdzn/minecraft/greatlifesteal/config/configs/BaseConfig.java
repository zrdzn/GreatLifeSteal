package io.github.zrdzn.minecraft.greatlifesteal.config.configs;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.BooleanProperty;
import ch.jalu.configme.properties.DoubleProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.PropertyBuilder;
import ch.jalu.configme.properties.types.BeanPropertyType;
import io.github.zrdzn.minecraft.greatlifesteal.action.ActionType;
import io.github.zrdzn.minecraft.greatlifesteal.config.bean.BeanBuilder;
import io.github.zrdzn.minecraft.greatlifesteal.config.bean.beans.ActionBean;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Represents 'baseSettings' section.
 */
public class BaseConfig implements SettingsHolder {

    @Comment("Amount of maximum health points that will be given to the new player that did not play before.")
    public static final Property<Double> DEFAULT_HEALTH = new DoubleProperty(
            "baseSettings.defaultHealth",
            20.0D
    );

    @Comment("Minimum amount of health points a player can have.")
    public static final Property<Double> MINIMUM_HEALTH = new DoubleProperty(
            "baseSettings.minimumHealth",
            2.0D
    );

    @Comment("Maximum amount of health points a player can have.")
    public static final Property<Double> MAXIMUM_HEALTH = new DoubleProperty(
            "baseSettings.maximumHealth",
            40.0D
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

    public static final List<String> DEFAULT_BROADCAST_MESSAGE = Collections.singletonList(
            "&aPlayer &e{victim} ({victim_max_health} hp) &ahas been eliminated by &e{killer} ({killer_max_health} hp)&a."
    );
    public static final List<String> DEFAULT_DISPATCH_COMMANDS = Collections.singletonList("tempban {victim} 7d");

    @Comment({
            "Define what list of actions should happen if a player reaches specific amount of maximum health points.",
            "",
            "If the action should be enabled.",
            "enabled: false",
            "",
            "Action type that should be done on reaching health points goal.",
            "If you are using SPECTATOR_MODE, switch to DISPATCH_COMMANDS instead because the previous action is",
            "deprecated and will be removed in the future.",
            " DISPATCH_COMMANDS - execute a list of commands as a console.",
            " BROADCAST - broadcast a message that is specified in the parameters list.",
            "type: DISPATCH_COMMANDS",
            "",
            "Amount of health points that are needed to execute the action.",
            "activateAtHealth: 4",
            "",
            "Delay in ticks after which the action should be executed.",
            "Very low values such as 0 or 1 may lead to performance issues, so choose wisely.",
            "20 ticks = 1 second.",
            "delay: 5",
            "",
            "List of parameters that are adequate to the chosen action.",
            " DISPATCH_COMMANDS - list of commands.",
            " BROADCAST - list of messages.",
            "Placeholders:",
            " {killer} - represents killer username, or last damage cause (if killByPlayerOnly not active)",
            " {victim} - represents victim username",
            " {killer_max_health} - represents killer's max health",
            " {victim_max_health} - represents victim's max health",
            "parameters:",
            "- gamemode spectator {victim}"
    })
    public static final Property<Map<String, ActionBean>> CUSTOM_ACTIONS = new PropertyBuilder
            .MapPropertyBuilder<>(BeanPropertyType.of(ActionBean.class))
            .path("baseSettings.customActions")
            .defaultEntry("announce", BeanBuilder
                    .from(ActionBean.class)
                    .with(announce -> announce.setEnabled(true))
                    .with(announce -> announce.setType(ActionType.BROADCAST))
                    .with(announce -> announce.setParameters(DEFAULT_BROADCAST_MESSAGE))
                    .build())
            .defaultEntry("eliminate", BeanBuilder
                    .from(ActionBean.class)
                    .with(eliminate -> eliminate.setType(ActionType.DISPATCH_COMMANDS))
                    .with(eliminate -> eliminate.setParameters(DEFAULT_DISPATCH_COMMANDS))
                    .build())
            .build();

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
