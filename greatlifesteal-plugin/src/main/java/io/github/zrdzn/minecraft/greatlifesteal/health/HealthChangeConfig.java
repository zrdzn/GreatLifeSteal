package io.github.zrdzn.minecraft.greatlifesteal.health;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class HealthChangeConfig extends OkaeriConfig {

    @Comment("How many health points should be deducted from the victim.")
    private double victim = 2.0D;

    @Comment("How many health points should the killer be given.")
    private double killer = 2.0D;

    public double getVictim() {
        return this.victim;
    }

    public void setVictim(double victim) {
        this.victim = victim;
    }

    public double getKiller() {
        return this.killer;
    }

    public void setKiller(double killer) {
        this.killer = killer;
    }

}
