package io.github.zrdzn.minecraft.greatlifesteal.user;

import io.github.zrdzn.minecraft.greatlifesteal.config.PluginConfig;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Map.Entry;

public class UserListener implements Listener {

    private final PluginConfig config;
    private final DamageableAdapter adapter;

    public UserListener(PluginConfig config, DamageableAdapter adapter) {
        this.config = config;
        this.adapter = adapter;
    }

    @EventHandler
    public void changePlayerHearts(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        Entry<Integer, Integer> healthRange = this.config.getHealthRange();

        int healthChange = this.config.getHealthChange();

        if (this.config.isKillByPlayerOnly()) {
            if (killer == null) {
                return;
            }

            double killerNewHealth = this.adapter.getMaxHealth(killer) + healthChange;
            if (killerNewHealth <= healthRange.getValue()) {
                this.adapter.setMaxHealth(killer, killerNewHealth);
            }
        }

        double victimNewHealth = this.adapter.getMaxHealth(victim) - healthChange;
        if (victimNewHealth >= healthRange.getKey()) {
            this.adapter.setMaxHealth(victim, victimNewHealth);
        }
    }

}
