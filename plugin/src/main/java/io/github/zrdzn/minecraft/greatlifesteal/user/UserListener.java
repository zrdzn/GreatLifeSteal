package io.github.zrdzn.minecraft.greatlifesteal.user;

import io.github.zrdzn.minecraft.greatlifesteal.GreatLifeStealPlugin;
import io.github.zrdzn.minecraft.greatlifesteal.configs.EliminationConfig;
import io.github.zrdzn.minecraft.greatlifesteal.configs.PluginConfig;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartItem;
import io.github.zrdzn.minecraft.greatlifesteal.message.MessageService;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.stream.Collectors;

public class UserListener implements Listener {

    private final PluginConfig config;
    private final DamageableAdapter adapter;
    private final HeartItem heartItem;
    private final boolean latestVersion;

    public UserListener(PluginConfig config, DamageableAdapter adapter, HeartItem heartItem, boolean latestVersion) {
        this.config = config;
        this.adapter = adapter;
        this.heartItem = heartItem;
        this.latestVersion = latestVersion;
    }

    @EventHandler
    public void notifyPermittedPlayer(PlayerJoinEvent event) {
        if (this.latestVersion) {
            return;
        }

        Player player = event.getPlayer();
        if (player.hasPermission("greatlifesteal.notify.update")) {
            MessageService.send(player, this.config.messages.pluginOutdated);
        }
    }

    @EventHandler
    public void setDefaultHearts(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            this.adapter.setMaxHealth(player, this.config.baseSettings.defaultHealth);
        }
    }

    @EventHandler
    public void changePlayerHearts(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        int healthChange = this.config.baseSettings.healthChange;

        if (this.config.baseSettings.giveHealthToKiller && this.config.baseSettings.killByPlayerOnly) {
            if (killer == null) {
                return;
            }

            double killerNewHealth = this.adapter.getMaxHealth(killer) + healthChange;
            if (killerNewHealth <= this.config.baseSettings.maximumHealth) {
                this.adapter.setMaxHealth(killer, killerNewHealth);
            } else {
                MessageService.send(killer, this.config.messages.maxHealthReached);

                HeartItem heartItem = this.heartItem;
                if (heartItem != null && this.config.baseSettings.heartItem.rewardHeartOnOverlimit) {
                    killer.getInventory().addItem(heartItem.getCraftingRecipe().getResult());
                }
            }
        }

        double victimMaxHealth = this.adapter.getMaxHealth(victim);

        double victimNewHealth = victimMaxHealth - healthChange;
        if (this.config.baseSettings.takeHealthFromVictim && victimNewHealth >= this.config.baseSettings.minimumHealth) {
            this.adapter.setMaxHealth(victim, victimNewHealth);
        }

        EliminationConfig elimination = this.config.baseSettings.eliminationMode;
        if (!elimination.enabled) {
            return;
        }

        if (victimMaxHealth <= elimination.requiredHealth) {
            switch (elimination.action) {
                case SPECTATOR_MODE:
                    victim.setGameMode(GameMode.SPECTATOR);
                    break;
                case DISPATCH_COMMANDS:
                    for (String command : elimination.commands) {
                        command = StringUtils.replace(command, "{victim}", victim.getName());
                        if (killer != null) {
                            command = StringUtils.replace(command, "{killer}", killer.getName());
                        }
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    }
                    break;
                case BROADCAST:
                    elimination.broadcastMessages.stream()
                        .map(message -> StringUtils.replace(message, "{player}", victim.getName()))
                        .map(GreatLifeStealPlugin::formatColor)
                        .forEach(Bukkit::broadcastMessage);
            }
        }
    }

}
