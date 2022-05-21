package io.github.zrdzn.minecraft.greatlifesteal.config;

import java.util.Map.Entry;

public class PluginConfig {

    private final int healthChange;
    private final Entry<Integer, Integer> healthRange;
    private final boolean killByPlayerOnly;

    public PluginConfig(int healthChange, Entry<Integer, Integer> healthRange, boolean killByPlayerOnly) {
        this.healthChange = healthChange;
        this.healthRange = healthRange;
        this.killByPlayerOnly = killByPlayerOnly;
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

}
