package io.github.zrdzn.minecraft.greatlifesteal.configs;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.validator.annotation.Positive;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationModeAction;

import java.util.Collections;
import java.util.List;

public class EliminationConfig extends OkaeriConfig {

    @Comment("If any action below should be executed.")
    public boolean enabled = false;

    @Positive
    @Comment("")
    @Comment("Amount of health points that are needed to execute an action.")
    public int requiredHealth = 4;

    @Comment("")
    @Comment("Action that should be done on reaching health points goal.")
    @Comment("Available:")
    @Comment(" SPECTATOR_MODE - change player's game mode to spectator.")
    @Comment(" DISPATCH_COMMANDS - execute a list of commands as a console.")
    @Comment(" BROADCAST - broadcast a message that is specified below.")
    public EliminationModeAction action = EliminationModeAction.SPECTATOR_MODE;

    @Comment("")
    @Comment("If using DISPATCH_COMMANDS action, specify commands to execute below.")
    @Comment("Placeholders:")
    @Comment(" {killer} - represents killer username")
    @Comment(" {victim} - represents victim username")
    @Comment(" {killer_max_health} - represents killer's max health")
    @Comment(" {victim_max_health} - represents victim's max health")
    public List<String> commands = Collections.singletonList("ban {victim}");

    @Comment("")
    @Comment("Message that should be broadcasted on the elimination.")
    @Comment("Works only if the action is set to BROADCAST.")
    @Comment("Placeholders:")
    @Comment(" {killer} - represents killer username")
    @Comment(" {victim} - represents victim username")
    @Comment(" {killer_max_health} - represents killer's max health")
    @Comment(" {victim_max_health} - represents victim's max health")
    public List<String> broadcastMessages = Collections.singletonList(
        "&aPlayer &e{victim} ({victim_max_health} hp) &ahas been eliminated by &e{killer} ({killer_max_health} hp)&a.");

}
