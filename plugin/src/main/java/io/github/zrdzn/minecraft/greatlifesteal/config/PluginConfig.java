package io.github.zrdzn.minecraft.greatlifesteal.config;

import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationMode;

import java.util.Map.Entry;

public class PluginConfig {

    private final int defaultHealth;
    private final int healthChange;
    private final Entry<Integer, Integer> healthRange;
    private final boolean killByPlayerOnly;
    private final EliminationMode eliminationMode;

    public PluginConfig(int defaultHealth, int healthChange, Entry<Integer, Integer> healthRange,
                        boolean killByPlayerOnly, EliminationMode eliminationMode) {
        this.defaultHealth = defaultHealth;
        this.healthChange = healthChange;
        this.healthRange = healthRange;
        this.killByPlayerOnly = killByPlayerOnly;
        this.eliminationMode = eliminationMode;
    }

    public int getDefaultHealth() {
        return this.defaultHealth;
    }

    public int getHealthChange() {
        return this.healthChange;
    }

    public Entry<Integer, Integer> getHealthRange() {
        return this.healthRange;
    }

    public boolean isKillByPlayerOnly() {
        return this.killByPlayerOnly;
    }

    public EliminationMode getEliminationMode() {
        return this.eliminationMode;
    }

}
