package io.github.zrdzn.minecraft.greatlifesteal.user;

import io.github.zrdzn.minecraft.greatlifesteal.config.PluginConfig;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationMode;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map.Entry;

public class UserListener implements Listener {

    private final PluginConfig config;
    private final DamageableAdapter adapter;

    public UserListener(PluginConfig config, DamageableAdapter adapter) {
        this.config = config;
        this.adapter = adapter;
    }

    @EventHandler
    public void setDefaultHearts(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            this.adapter.setMaxHealth(player, this.config.getDefaultHealth());
        }
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
            } else {
                if (this.config.getHeartItem() != null && this.config.isGiveToKillerOverlimit()) {
                    killer.getInventory().addItem(this.config.getHeartItem().getCraftingRecipe().getResult());
                }
            }
        }

        double victimMaxHealth = this.adapter.getMaxHealth(victim);

        double victimNewHealth = victimMaxHealth - healthChange;
        if (victimNewHealth >= healthRange.getKey()) {
            this.adapter.setMaxHealth(victim, victimNewHealth);
        }

        EliminationMode elimination = this.config.getEliminationMode();
        if (elimination == null) {
            return;
        }

        if (victimMaxHealth <= elimination.getRequiredHealth()) {
            switch (elimination.getAction()) {
                case SPECTATOR_MODE:
                    victim.setGameMode(GameMode.SPECTATOR);
                    break;
                case DISPATCH_COMMANDS:
                    for (String command : elimination.getActionCommands()) {
                        command = StringUtils.replace(command, "{victim}", victim.getName());
                        if (killer != null) {
                            command = StringUtils.replace(command, "{killer}", killer.getName());
                        }
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    }
                    break;
            }
        }
    }

}
