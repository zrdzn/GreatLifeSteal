package io.github.zrdzn.minecraft.greatlifesteal.config;

import java.util.Map.Entry;

public class PluginConfig {

    private final int defaultHealth;
    private final int healthChange;
    private final Entry<Integer, Integer> healthRange;

    public PluginConfig(int defaultHealth, int healthChange, Entry<Integer, Integer> healthRange) {
        this.defaultHealth = defaultHealth;
        this.healthChange = healthChange;
        this.healthRange = healthRange;
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

}
