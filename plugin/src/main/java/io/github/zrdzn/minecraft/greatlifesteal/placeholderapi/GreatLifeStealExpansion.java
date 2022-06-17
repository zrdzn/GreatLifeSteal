package io.github.zrdzn.minecraft.greatlifesteal.placeholderapi;

import ch.jalu.configme.SettingsManager;
import io.github.zrdzn.minecraft.greatlifesteal.config.beans.ActionBean;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.BaseConfig;
import io.github.zrdzn.minecraft.greatlifesteal.health.HealthCache;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import java.text.DecimalFormat;
import java.util.Arrays;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class GreatLifeStealExpansion extends PlaceholderExpansion {

    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    private final SettingsManager config;
    private final DamageableAdapter adapter;
    private final Server server;
    private final HealthCache cache;

    public GreatLifeStealExpansion(SettingsManager config, DamageableAdapter adapter, Server server, HealthCache cache) {
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

        ActionBean action = this.config.getProperty(BaseConfig.CUSTOM_ACTIONS)
                .get(parametersSplitted[parametersSplitted.length - 2]);

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

        String placeholderKey = String.join("_", Arrays.copyOf(parametersSplitted, parametersSplitted.length - 1));
        switch (placeholderKey.toLowerCase()) {
            case "lives":
                if (action == null) {
                    return null;
                }

                int lives = 0;
                if (maxHealth > action.getActivateAtHealth()) {
                    int healthChange = this.config.getProperty(BaseConfig.HEALTH_CHANGE);
                    lives = (int) Math.ceil((maxHealth - action.getActivateAtHealth()) / healthChange);
                }

                return String.valueOf(lives);
            case "hearts":
                return this.decimalFormat.format(maxHealth / 2.0D);
            case "health":
                return this.decimalFormat.format(maxHealth);
            case "hearts_left":
                if (action == null) {
                    return null;
                }

                double heartsLeft = 0.0D;
                if (maxHealth > action.getActivateAtHealth()) {
                    heartsLeft = (maxHealth - action.getActivateAtHealth()) / 2;
                }

                return this.decimalFormat.format(heartsLeft);
            case "health_left":
                if (action == null) {
                    return null;
                }

                double healthLeft = 0.0D;
                if (maxHealth > action.getActivateAtHealth()) {
                    healthLeft = (maxHealth - action.getActivateAtHealth());
                }

                return this.decimalFormat.format(healthLeft);
            default:
                return null;
        }
    }

}
