package io.github.zrdzn.minecraft.greatlifesteal.placeholderapi;

import io.github.zrdzn.minecraft.greatlifesteal.configs.BaseSettingsConfig;
import io.github.zrdzn.minecraft.greatlifesteal.health.HealthCache;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
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
                if (!this.config.eliminationMode.enabled) {
                    return null;
                }

                int lives = 0;
                if (maxHealth > this.config.eliminationMode.requiredHealth) {
                    lives = (int) Math.ceil((maxHealth - this.config.eliminationMode.requiredHealth) / this.config.healthChange);
                }

                return String.valueOf(lives);
            case "hearts":
                return decimalFormat.format(maxHealth / 2.0D);
            case "health":
                return decimalFormat.format(maxHealth);
            case "hearts_left":
                if (!this.config.eliminationMode.enabled) {
                    return null;
                }

                int heartsLeft = 0;
                if (maxHealth > this.config.eliminationMode.requiredHealth) {
                    heartsLeft = (int) (maxHealth - this.config.eliminationMode.requiredHealth) / 2;
                }

                return String.valueOf(heartsLeft);
            case "health_left":
                if (!this.config.eliminationMode.enabled) {
                    return null;
                }

                double healthLeft = 0;
                if (maxHealth > this.config.eliminationMode.requiredHealth) {
                    healthLeft = (maxHealth - this.config.eliminationMode.requiredHealth);
                }

                return decimalFormat.format(healthLeft);
            default:
                return null;
        }
    }

}
