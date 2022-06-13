package io.github.zrdzn.minecraft.greatlifesteal.user;

import io.github.zrdzn.minecraft.greatlifesteal.GreatLifeStealPlugin;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.PluginConfig;
import io.github.zrdzn.minecraft.greatlifesteal.health.HealthCache;
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
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class UserListener implements Listener {

    private final Map<Player, Entry<Player, Instant>> stealCooldowns = new HashMap<>();

    private final PluginConfig config;
    private final DamageableAdapter adapter;
    private final HealthCache cache;
    private final HeartItem heartItem;
    private final boolean latestVersion;

    public UserListener(PluginConfig config, DamageableAdapter adapter, HealthCache cache, HeartItem heartItem,
                        boolean latestVersion) {
        this.config = config;
        this.adapter = adapter;
        this.cache = cache;
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

        // (PAPI) this.cache.removeHealth(player.getName());
    }

    @EventHandler
    public void removeFromCache(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // (PAPI) this.cache.addHealth(player.getName(), player.getMaxHealth());
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

            if (this.config.baseSettings.ignoreSameIp) {
                if (victim.getAddress().getAddress().equals(killer.getAddress().getAddress())) {
                    return;
                }
            }

            // Checks if there is any cooldown active on the killer.
            if (this.config.baseSettings.stealCooldown.enabled) {
                Entry<Player, Instant> cooldownEntry = this.stealCooldowns.get(killer);
                if (cooldownEntry != null && victim.equals(cooldownEntry.getKey())) {
                    Duration difference = Duration.between(cooldownEntry.getValue(), Instant.now());
                    Duration limit = Duration.ofSeconds(this.config.baseSettings.stealCooldown.cooldown);
                    if (difference.compareTo(limit) < 0) {
                        String[] placeholders = { "{AMOUNT}", String.valueOf(limit.getSeconds() - difference.getSeconds()) };
                        MessageService.send(killer, this.config.messages.stealCooldownActive, placeholders);
                        return;
                    }
                }

                this.stealCooldowns.put(killer, new SimpleImmutableEntry<>(victim, Instant.now()));
            }

            double killerNewHealth = this.adapter.getMaxHealth(killer) + healthChange;
            if (killerNewHealth <= this.config.baseSettings.maximumHealth) {
                this.adapter.setMaxHealth(killer, killerNewHealth);
            } else {
                MessageService.send(killer, this.config.messages.maxHealthReached);

                HeartItem heartItem = this.heartItem;
                if (heartItem != null && this.config.baseSettings.heartItem.rewardHeartOnOverlimit) {
                    killer.getInventory().addItem(heartItem.getResult());
                }
            }
        }

        double victimMaxHealth = this.adapter.getMaxHealth(victim);

        double victimNewHealth = victimMaxHealth - healthChange;
        if (this.config.baseSettings.takeHealthFromVictim && victimNewHealth >= this.config.baseSettings.minimumHealth) {
            this.adapter.setMaxHealth(victim, victimNewHealth);
        }

        if (this.config.baseSettings.customActions.isEmpty()) {
            return;
        }

        this.config.baseSettings.customActions.forEach((actionKey, action) -> {
            if (victimMaxHealth - healthChange > action.requiredHealth) {
                return;
            }

            switch (action.type) {
                case SPECTATOR_MODE:
                    victim.setGameMode(GameMode.SPECTATOR);
                    break;
                case DISPATCH_COMMANDS:
                    action.parameters.forEach(command -> {
                        command = this.formatPlaceholders(command, victim, killer);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    });

                    break;
                case BROADCAST:
                    action.parameters.stream()
                        .map(message -> this.formatPlaceholders(message, victim, killer))
                        .map(message -> StringUtils.replace(message, "{player}", victim.getName()))
                        .map(GreatLifeStealPlugin::formatColor)
                        .forEach(Bukkit::broadcastMessage);
            }
        });
    }

    private String formatPlaceholders(String string, Player victim, Player killer) {
        string = StringUtils.replaceEach(string,
            new String[] { "{victim}", "{victim_max_health}" },
            new String[] { victim.getName(), String.valueOf((int) victim.getMaxHealth()) });
        if (killer != null) {
            string = StringUtils.replaceEach(string,
                new String[] { "{killer}", "{killer_max_health}" },
                new String[] { killer.getName(), String.valueOf((int) killer.getMaxHealth()) });
        }

        return string;
    }

}
