package io.github.zrdzn.minecraft.greatlifesteal;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import io.github.zrdzn.minecraft.greatlifesteal.action.ActionType;
import io.github.zrdzn.minecraft.greatlifesteal.action.ActionConfig;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationReviveConfig;
import io.github.zrdzn.minecraft.greatlifesteal.health.HealthConfig;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartConfig;
import io.github.zrdzn.minecraft.greatlifesteal.message.MessageConfig;
import io.github.zrdzn.minecraft.greatlifesteal.storage.StorageConfig;

public class PluginConfig extends OkaeriConfig {

    public static class StealCooldownConfig extends OkaeriConfig {

        @Comment("If the life steal cooldown should be enabled on the server.")
        private boolean enabled = true;

        @Comment("Cooldown time in seconds.")
        private int cooldown = 30;

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getCooldown() {
            return this.cooldown;
        }

        public void setCooldown(int cooldown) {
            this.cooldown = cooldown;
        }

    }

    public static class DisabledWorldsConfig extends OkaeriConfig {

        @Comment("Specify in which worlds gaining/losing health should be disabled.")
        private List<String> healthChange = Collections.singletonList("custom_world");

        @Comment("Specify in which worlds eliminations should be disabled.")
        @Comment("Example: If you want to allow the eliminated player X from world Y to access world Z, list it here.")
        private List<String> eliminations = Collections.singletonList("custom_world");

        public List<String> getHealthChange() {
            return this.healthChange;
        }

        public void setHealthChange(List<String> healthChange) {
            this.healthChange = healthChange;
        }

        public List<String> getEliminations() {
            return this.eliminations;
        }

        public void setEliminations(List<String> eliminations) {
            this.eliminations = eliminations;
        }

    }

    @Comment("All general health related settings.")
    @Comment("---------------------------")
    @Comment("1 heart = 2 health points")
    @Comment("---------------------------")
    private HealthConfig health = new HealthConfig();

    @Comment("Health points will only be changed if the player is killed by another player.")
    private boolean killByPlayerOnly = true;

    @Comment("If players with the same IP address should be prevented from gaining health points.")
    @Comment("This option can prevent farming health points via multiple accounts on a single device.")
    private boolean ignoreSameIp = false;

    @Comment("Specify whether life steal cooldown should be enabled and how long it should last.")
    @Comment("Killers will not be able to take hearts from victims unless a cooldown time has expired.")
    private StealCooldownConfig stealCooldown = new StealCooldownConfig();

    @Comment("Specify in which worlds each system should be disabled or where it will not have any impact.")
    private DisabledWorldsConfig disabledWorlds = new DisabledWorldsConfig();

    @Comment("An item that can be used by a player to increase their maximum health by a specific amount.")
    private HeartConfig heart = new HeartConfig();

    private static final List<String> DEFAULT_BROADCAST_MESSAGE = Collections.singletonList(
            "&aPlayer &e{victim} ({victim_max_health} hp) &ahas been eliminated by &e{killer} ({killer_max_health} hp)&a."
    );
    private static final List<String> DEFAULT_TEMPBAN_COMMAND = Collections.singletonList("tempban {victim} 7d");
    private static final List<String> DEFAULT_GAMEMODE_SET = Collections.singletonList("gamemode spectator {victim}");
    private static final List<String> DEFAULT_BAN_REASON = Collections.singletonList("&cYou have been eliminated!");
    private static final List<String> DEFAULT_REVIVE_COMMAND = Collections.singletonList("unban {victim}");

    @Comment("Define the list of actions that should happen when a player reaches a specific amount of maximum health.")
    @Comment("")
    @Comment("If the action should be enabled.")
    @Comment("enabled: false")
    @Comment("")
    @Comment("Action type that should be taken upon reaching the health goal.")
    @Comment("If you are using SPECTATOR_MODE, switch to DISPATCH_COMMANDS instead, as the previous action is deprecated and will be removed in the future.")
    @Comment(" DISPATCH_COMMANDS - execute a list of commands as a console.")
    @Comment(" BROADCAST - broadcast a message that is specified in the parameters list.")
    @Comment(" BAN - bans a player with a specified message (you should use DISPATCH_COMMANDS for custom punishments)")
    @Comment("type: DISPATCH_COMMANDS")
    @Comment("")
    @Comment("Amount of health points that are needed to execute the action.")
    @Comment("activateAtHealth: 4")
    @Comment("")
    @Comment("Delay in ticks after which the action should be executed.")
    @Comment("Very low values such as 0 or 1 may lead to performance issues, so choose wisely.")
    @Comment("20 ticks = 1 second.")
    @Comment("delay: 5")
    @Comment("")
    @Comment("List of parameters that are adequate to the chosen action.")
    @Comment(" DISPATCH_COMMANDS - list of commands.")
    @Comment(" BROADCAST - list of messages.")
    @Comment(" BAN - list of lines for ban reason.")
    @Comment("Placeholders:")
    @Comment(" {killer} - represents killer username, or last damage cause (if killByPlayerOnly is not active)")
    @Comment(" {victim} - represents victim username")
    @Comment(" {killer_max_health} - represents killer's max health")
    @Comment(" {victim_max_health} - represents victim's max health")
    @Comment("parameters:")
    @Comment("- gamemode spectator {victim}")
    @Comment("")
    @Comment("Specify whether or not you want to have a revive available for that action.")
    @Comment("If enabled, the commands will be executed after a revive execution.")
    @Comment("For example, if the action is a ban command, you can provide an unban command for the revive.")
    @Comment("Allowed action types that works with the revive system: DISPATCH_COMMANDS")
    @Comment("revive:")
    @Comment("  If the revive for this action should be enabled.")
    @Comment("  enabled: true")
    @Comment("")
    @Comment("  List of commands that should be executed after a revive execution.")
    @Comment("  For the BAN action type 'commands' are not used because the elimination is removed internally.")
    @Comment("  Allowed action types that works with revive commands: DISPATCH_COMMANDS")
    @Comment("  Placeholders:")
    @Comment("  {victim} - represents victim username")
    @Comment("  commands:")
    @Comment("  - unban {victim}")
    private Map<String, ActionConfig> actions = new HashMap<String, ActionConfig>() {{
        this.put("announce", new ActionConfig(true, ActionType.BROADCAST, DEFAULT_BROADCAST_MESSAGE, new EliminationReviveConfig()));
        this.put("spectate", new ActionConfig(ActionType.DISPATCH_COMMANDS, DEFAULT_GAMEMODE_SET, new EliminationReviveConfig()));
        this.put("eliminatePerm", new ActionConfig(ActionType.BAN, DEFAULT_BAN_REASON, new EliminationReviveConfig(true)));
        this.put("eliminateTemp", new ActionConfig(ActionType.DISPATCH_COMMANDS, DEFAULT_TEMPBAN_COMMAND, new EliminationReviveConfig(true, DEFAULT_REVIVE_COMMAND)));
    }};

    private StorageConfig storage = new StorageConfig();

    private MessageConfig messages = new MessageConfig();

    public HealthConfig getHealth() {
        return this.health;
    }

    public void setHealth(HealthConfig health) {
        this.health = health;
    }

    public boolean isKillByPlayerOnly() {
        return this.killByPlayerOnly;
    }

    public void setKillByPlayerOnly(boolean killByPlayerOnly) {
        this.killByPlayerOnly = killByPlayerOnly;
    }

    public boolean isIgnoreSameIp() {
        return this.ignoreSameIp;
    }

    public void setIgnoreSameIp(boolean ignoreSameIp) {
        this.ignoreSameIp = ignoreSameIp;
    }

    public StealCooldownConfig getStealCooldown() {
        return this.stealCooldown;
    }

    public void setStealCooldown(StealCooldownConfig stealCooldown) {
        this.stealCooldown = stealCooldown;
    }

    public DisabledWorldsConfig getDisabledWorlds() {
        return this.disabledWorlds;
    }

    public void setDisabledWorlds(DisabledWorldsConfig disabledWorlds) {
        this.disabledWorlds = disabledWorlds;
    }

    public HeartConfig getHeart() {
        return this.heart;
    }

    public void setHeart(HeartConfig heart) {
        this.heart = heart;
    }

    public Map<String, ActionConfig> getActions() {
        return this.actions;
    }

    public void setActions(Map<String, ActionConfig> actions) {
        this.actions = actions;
    }

    public StorageConfig getStorage() {
        return this.storage;
    }

    public void setStorage(StorageConfig storage) {
        this.storage = storage;
    }

    public MessageConfig getMessages() {
        return this.messages;
    }

    public void setMessages(MessageConfig messages) {
        this.messages = messages;
    }

}
