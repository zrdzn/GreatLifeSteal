package io.github.zrdzn.minecraft.greatlifesteal.placeholderapi;

import io.github.zrdzn.minecraft.greatlifesteal.config.configs.ActionConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.BaseSettingsConfig;
import io.github.zrdzn.minecraft.greatlifesteal.health.HealthCache;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Arrays;

public class GreatLifeStealExpansion extends PlaceholderExpansion {

    private final BaseSettingsConfig config;
    private final DamageableAdapter adapter;
    private final Server server;
    private final HealthCache cache;

    public GreatLifeStealExpansion(BaseSettingsConfig config, DamageableAdapter adapter, Server server, HealthCache cache) {
        this.config = config;
        this.adapter = adapter;
        this.server = server;
        this.cache = cache;
    }

    @Override
    public String getAuthor() {
        return "zrdzn, some0ne3";
    }

    @Override
    public String getIdentifier() {
        return "glifesteal";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String parameters) {
        String[] parametersSplitted = parameters.split("_");

        ActionConfig action = this.config.customActions.get(parametersSplitted[parametersSplitted.length - 2]);

        String targetName = parametersSplitted[parametersSplitted.length - 1];

        double maxHealth;

        Player target = this.server.getPlayer(targetName);
        if (target == null) {
            /*
            if (!this.cache.getHealths().containsKey(targetName)) {
                return null;
            }

            maxHealth = this.cache.getHealth(targetName);
            */
            return null;
        } else {
            maxHealth = this.adapter.getMaxHealth(target);
        }

        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        String placeholderKey = String.join("_", Arrays.copyOf(parametersSplitted, parametersSplitted.length - 1));
        switch (placeholderKey.toLowerCase()) {
            case "lives":
                if (action == null) {
                    return null;
                }

                int lives = 0;
                if (maxHealth > action.requiredHealth) {
                    lives = (int) Math.ceil((maxHealth - action.requiredHealth) / this.config.healthChange);
                }

                return String.valueOf(lives);
            case "hearts":
                return decimalFormat.format(maxHealth / 2.0D);
            case "health":
                return decimalFormat.format(maxHealth);
            case "hearts_left":
                if (action == null) {
                    return null;
                }

                double heartsLeft = 0.0D;
                if (maxHealth > action.requiredHealth) {
                    heartsLeft = (maxHealth - action.requiredHealth) / 2;
                }

                return decimalFormat.format(heartsLeft);
            case "health_left":
                if (action == null) {
                    return null;
                }

                double healthLeft = 0.0D;
                if (maxHealth > action.requiredHealth) {
                    healthLeft = (maxHealth - action.requiredHealth);
                }

                return decimalFormat.format(healthLeft);
            default:
                return null;
        }
    }

}
