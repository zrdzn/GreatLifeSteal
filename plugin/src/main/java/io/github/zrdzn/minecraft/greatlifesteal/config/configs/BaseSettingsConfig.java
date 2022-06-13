package io.github.zrdzn.minecraft.greatlifesteal.config.configs;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.validator.annotation.Positive;
import eu.okaeri.validator.annotation.PositiveOrZero;
import io.github.zrdzn.minecraft.greatlifesteal.action.Action;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BaseSettingsConfig extends OkaeriConfig {

    @Comment("If health points should be decreased from a player who is killed.")
    public boolean takeHealthFromVictim = true;

    @Comment("")
    @Comment("If health points should be increased for a player who killed somebody.")
    public boolean giveHealthToKiller = true;

    @Positive
    @Comment("")
    @Comment("Amount of maximum health points that will be given to the new player that did not play before.")
    public int defaultHealth = 20;

    @PositiveOrZero
    @Comment("")
    @Comment("Amount of health points revoked when a player was killed or awarded when a kill was scored.")
    public int healthChange = 2;

    @Positive
    @Comment("")
    @Comment("Minimum amount of health points a player can have.")
    public int minimumHealth = 2;

    @Positive
    @Comment("")
    @Comment("Maximum amount of health points a player can have.")
    public int maximumHealth = 40;

    @Comment("")
    @Comment("Health points will be changed only if the player was killed by the other player.")
    public boolean killByPlayerOnly = true;

    @Comment("")
    @Comment("If players with same ip address should be prevented from gaining health points.")
    @Comment("This option can prevent from farming health points via multi accounts on a single device.")
    public boolean ignoreSameIp = true;

    @Comment("")
    @Comment("Specify if life steal cooldown should be enabled and how long should it last.")
    @Comment("Killers will not be able to take hearts from victims unless a cooldown time expires.")
    public StealCooldownConfig stealCooldown = new StealCooldownConfig();

    @Comment("")
    @Comment("Item that can be used by a player to give him a specified amount of health points.")
    public HeartItemConfig heartItem = new HeartItemConfig();

    @Comment("")
    @Comment("Define what list of actions should happen if a player reaches specific amount of maximum health points.")
    public Map<String, ActionConfig> customActions = new HashMap<String, ActionConfig>() {{
        ActionConfig announce = new ActionConfig();
        announce.enabled = true;
        announce.type = Action.BROADCAST;
        announce.parameters = Collections.singletonList("&aPlayer &e{victim} ({victim_max_health} hp) &ahas been eliminated by &e{killer} ({killer_max_health} hp)&a.");

        ActionConfig eliminate = new ActionConfig();
        eliminate.type = Action.DISPATCH_COMMANDS;
        announce.parameters = Collections.singletonList("tempban {victim} 7d");

        this.put("announce", announce);
        this.put("eliminate", eliminate);
    }};

    public class StealCooldownConfig extends OkaeriConfig {

        @Comment("If the life steal cooldown should be enabled on the server.")
        public boolean enabled = true;

        @Comment("")
        @Comment("Cooldown time in seconds.")
        public int cooldown = 30;

    }

}
