package io.github.zrdzn.minecraft.greatlifesteal.configs;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.validator.annotation.Positive;
import eu.okaeri.validator.annotation.PositiveOrZero;

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
    public boolean preventTakeFromSameIp = true;

    @Comment("")
    @Comment("Item that can be used by a player to give him a specified amount of health points.")
    public HeartItemConfig heartItem = new HeartItemConfig();

    @Comment("")
    @Comment("Define what will happen if a player reaches specific amount of maximum health points.")
    public EliminationConfig eliminationMode = new EliminationConfig();

}
