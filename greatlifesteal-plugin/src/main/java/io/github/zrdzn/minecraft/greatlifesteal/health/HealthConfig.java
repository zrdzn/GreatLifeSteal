package io.github.zrdzn.minecraft.greatlifesteal.health;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class HealthConfig extends OkaeriConfig {

    @Comment("Default amount of maximum health points given to new players who have not played before.")
    private double defaultMaximumHealth = 20.0D;

    @Comment("Minimum amount of health points a player can have.")
    private double minimumHealth = 2.0D;

    @Comment("Maximum amount of health points a player can have.")
    private double maximumHealth = 40.0D;

    @Comment("Health points are deducted when a player is killed or awarded when a kill is scored.")
    @Comment("If you wish to disable the life steal system for either the killer or the victim, simply set their health change to 0.")
    private HealthChangeConfig change = new HealthChangeConfig();

    public double getDefaultMaximumHealth() {
        return this.defaultMaximumHealth;
    }

    public void setDefaultMaximumHealth(double defaultMaximumHealth) {
        this.defaultMaximumHealth = defaultMaximumHealth;
    }

    public double getMinimumHealth() {
        return this.minimumHealth;
    }

    public void setMinimumHealth(double minimumHealth) {
        this.minimumHealth = minimumHealth;
    }

    public double getMaximumHealth() {
        return this.maximumHealth;
    }

    public void setMaximumHealth(double maximumHealth) {
        this.maximumHealth = maximumHealth;
    }

    public HealthChangeConfig getChange() {
        return this.change;
    }

    public void setChange(HealthChangeConfig change) {
        this.change = change;
    }

}
