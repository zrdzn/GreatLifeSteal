package io.github.zrdzn.minecraft.greatlifesteal.user;

import io.github.zrdzn.minecraft.greatlifesteal.config.PluginConfig;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationMode;
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

import java.util.Map.Entry;

public class UserListener implements Listener {

    private final PluginConfig config;
    private final MessageService messageService;
    private final DamageableAdapter adapter;
    private final boolean latestVersion;

    public UserListener(PluginConfig config, MessageService messageService, DamageableAdapter adapter,
                        boolean latestVersion) {
        this.config = config;
        this.messageService = messageService;
        this.adapter = adapter;
        this.latestVersion = latestVersion;
    }

    @EventHandler
    public void notifyPermittedPlayer(PlayerJoinEvent event) {
        if (this.latestVersion) {
            return;
        }

        Player player = event.getPlayer();
        if (player.hasPermission("greatlifesteal.notify.update")) {
            this.messageService.send(player, "pluginOutdated");
        }
    }

    @EventHandler
    public void setDefaultHearts(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            this.adapter.setMaxHealth(player, this.config.defaultHealth);
        }
    }

    @EventHandler
    public void changePlayerHearts(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        Entry<Integer, Integer> healthRange = this.config.healthRange;

        int healthChange = this.config.healthChange;

        if (this.config.giveHealthToKiller && this.config.killByPlayerOnly) {
            if (killer == null) {
                return;
            }

            double killerNewHealth = this.adapter.getMaxHealth(killer) + healthChange;
            if (killerNewHealth <= healthRange.getValue()) {
                this.adapter.setMaxHealth(killer, killerNewHealth);
            } else {
                this.messageService.send(killer, "maxHealthReached");

                HeartItem heartItem = this.config.heartItem;
                if (heartItem != null && this.config.rewardHeartOnOverlimit) {
                    killer.getInventory().addItem(heartItem.getCraftingRecipe().getResult());
                }
            }
        }

        double victimMaxHealth = this.adapter.getMaxHealth(victim);

        double victimNewHealth = victimMaxHealth - healthChange;
        if (this.config.takeHealthFromVictim && victimNewHealth >= healthRange.getKey()) {
            this.adapter.setMaxHealth(victim, victimNewHealth);
        }

        EliminationMode elimination = this.config.elimination;
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
