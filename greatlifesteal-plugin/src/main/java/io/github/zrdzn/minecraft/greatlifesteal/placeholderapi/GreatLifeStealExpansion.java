package io.github.zrdzn.minecraft.greatlifesteal.placeholderapi;

import java.text.DecimalFormat;
import java.util.Arrays;
import io.github.zrdzn.minecraft.greatlifesteal.PluginConfig;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationConfig;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GreatLifeStealExpansion extends PlaceholderExpansion {

    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    private final PluginConfig config;
    private final DamageableAdapter adapter;
    private final Server server;

    public GreatLifeStealExpansion(PluginConfig config, DamageableAdapter adapter, Server server) {
        this.config = config;
        this.adapter = adapter;
        this.server = server;
    }

    @Override
    public @NotNull String getAuthor() {
        return "zrdzn, some0ne3";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "glifesteal";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String parameters) {
        String[] parametersSplitted = parameters.split("_");

        EliminationConfig elimination = this.config.getEliminations().get(parametersSplitted[parametersSplitted.length - 2]);

        String targetName = parametersSplitted[parametersSplitted.length - 1];

        double maxHealth;

        Player target = this.server.getPlayer(targetName);
        if (target == null) {
            if (!player.isOnline()) return null;
            maxHealth = this.adapter.getMaxHealth(player.getPlayer());
        } else {
            maxHealth = this.adapter.getMaxHealth(target);
        }

        String placeholderKey = String.join("_", Arrays.copyOf(parametersSplitted, parametersSplitted.length - 1));
        switch (placeholderKey.toLowerCase()) {
            case "lives":
                if (elimination == null) {
                    return null;
                }

                int lives = 0;
                if (maxHealth > elimination.getActivateAtHealth()) {
                    double healthChange = this.config.getHealth().getChange().getVictim();
                    lives = (int) Math.ceil((maxHealth - elimination.getActivateAtHealth()) / healthChange);
                }

                return String.valueOf(lives);
            case "hearts":
                return this.decimalFormat.format(maxHealth / 2.0D);
            case "health":
                return this.decimalFormat.format(maxHealth);
            case "hearts_left":
                if (elimination == null) {
                    return null;
                }

                double heartsLeft = 0.0D;
                if (maxHealth > elimination.getActivateAtHealth()) {
                    heartsLeft = (maxHealth - elimination.getActivateAtHealth()) / 2;
                }

                return this.decimalFormat.format(heartsLeft);
            case "health_left":
                if (elimination == null) {
                    return null;
                }

                double healthLeft = 0.0D;
                if (maxHealth > elimination.getActivateAtHealth()) {
                    healthLeft = (maxHealth - elimination.getActivateAtHealth());
                }

                return this.decimalFormat.format(healthLeft);
            default:
                return null;
        }
    }

}
