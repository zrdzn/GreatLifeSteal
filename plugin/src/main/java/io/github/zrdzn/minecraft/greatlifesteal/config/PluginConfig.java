package io.github.zrdzn.minecraft.greatlifesteal.config;

import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartItem;

import java.util.Map.Entry;

public class PluginConfig {

    private final int defaultHealth;
    private final int healthChange;
    private final Entry<Integer, Integer> healthRange;
    private final boolean killByPlayerOnly;
    private final HeartItem heartItem;

    public PluginConfig(int defaultHealth, int healthChange, Entry<Integer, Integer> healthRange,
                        boolean killByPlayerOnly, HeartItem heartItem) {
        this.defaultHealth = defaultHealth;
        this.healthChange = healthChange;
        this.healthRange = healthRange;
        this.killByPlayerOnly = killByPlayerOnly;
        this.heartItem = heartItem;
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

    public HeartItem getHeartItem() {
        return this.heartItem;
    }

}
