package io.github.zrdzn.minecraft.greatlifesteal.config.configs;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.validator.annotation.Positive;
import io.github.zrdzn.minecraft.greatlifesteal.action.Action;

import java.util.Collections;
import java.util.List;

public class ActionConfig extends OkaeriConfig {

    @Comment("If the action should be enabled.")
    public boolean enabled = false;

    @Comment("")
    @Comment("Action type that should be done on reaching health points goal.")
    @Comment("If you are using SPECTATOR_MODE, switch to DISPATCH_COMMANDS")
    @Comment("instead because the previous action is deprecated and will be")
    @Comment("removed in the future.")
    @Comment("Available:")
    @Comment(" DISPATCH_COMMANDS - execute a list of commands as a console.")
    @Comment(" BROADCAST - broadcast a message that is specified in the parameters list.")
    public Action type = Action.DISPATCH_COMMANDS;

    @Positive
    @Comment("")
    @Comment("Amount of health points that are needed to execute the action.")
    public int activateAtHealth = 4;

    @Comment("")
    @Comment("List of parameters that are adequate to the chosen action.")
    @Comment(" DISPATCH_COMMANDS - list of commands.")
    @Comment(" BROADCAST - list of messages.")
    @Comment("Placeholders:")
    @Comment(" {killer} - represents killer username")
    @Comment(" {victim} - represents victim username")
    @Comment(" {killer_max_health} - represents killer's max health")
    @Comment(" {victim_max_health} - represents victim's max health")
    public List<String> parameters = Collections.singletonList("gamemode spectator {victim}");

}
