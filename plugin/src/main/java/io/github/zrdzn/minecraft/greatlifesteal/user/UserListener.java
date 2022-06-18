package io.github.zrdzn.minecraft.greatlifesteal.user;

import ch.jalu.configme.SettingsManager;
import io.github.zrdzn.minecraft.greatlifesteal.GreatLifeStealPlugin;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.BaseConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.MessagesConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.StealCooldownConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.heart.HeartConfig;
import io.github.zrdzn.minecraft.greatlifesteal.health.HealthCache;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartItem;
import io.github.zrdzn.minecraft.greatlifesteal.message.MessageService;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class UserListener implements Listener {

    private final Map<Player, Entry<Player, Instant>> stealCooldowns = new HashMap<>();

    private final SettingsManager config;
    private final DamageableAdapter adapter;
    private final HealthCache cache;
    private final HeartItem heartItem;
    private final boolean latestVersion;

    public UserListener(SettingsManager config, DamageableAdapter adapter, HealthCache cache, HeartItem heartItem,
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
            MessageService.send(player, this.config.getProperty(MessagesConfig.PLUGIN_OUTDATED));
        }
    }

    @EventHandler
    public void setDefaultHearts(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            this.adapter.setMaxHealth(player, this.config.getProperty(BaseConfig.DEFAULT_HEALTH));
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

        boolean giveHealthToKiller = this.config.getProperty(BaseConfig.GIVE_HEALTH_TO_KILLER);
        boolean killByPlayerOnly = this.config.getProperty(BaseConfig.KILL_BY_PLAYER_ONLY);

        int healthChange = this.config.getProperty(BaseConfig.HEALTH_CHANGE);

        if (giveHealthToKiller && killByPlayerOnly) {
            if (killer == null) {
                return;
            }

            if (this.config.getProperty(BaseConfig.IGNORE_SAME_IP)) {
                if (victim.getAddress().getAddress().equals(killer.getAddress().getAddress())) {
                    return;
                }
            }

            // Checks if there is any cooldown active on the killer.
            if (this.config.getProperty(StealCooldownConfig.ENABLED)) {
                Entry<Player, Instant> cooldownEntry = this.stealCooldowns.get(killer);
                if (cooldownEntry != null && victim.equals(cooldownEntry.getKey())) {
                    Duration difference = Duration.between(cooldownEntry.getValue(), Instant.now());
                    Duration limit = Duration.ofSeconds(this.config.getProperty(StealCooldownConfig.COOLDOWN));
                    if (difference.compareTo(limit) < 0) {
                        String[] placeholders = { "{AMOUNT}", String.valueOf(limit.getSeconds() - difference.getSeconds()) };
                        MessageService.send(killer, this.config.getProperty(MessagesConfig.STEAL_COOLDOWN_ACTIVE), placeholders);
                        return;
                    }
                }

                this.stealCooldowns.put(killer, new SimpleImmutableEntry<>(victim, Instant.now()));
            }

            double killerNewHealth = this.adapter.getMaxHealth(killer) + healthChange;
            if (killerNewHealth <= this.config.getProperty(BaseConfig.MAXIMUM_HEALTH)) {
                this.adapter.setMaxHealth(killer, killerNewHealth);
            } else {
                MessageService.send(killer, this.config.getProperty(MessagesConfig.MAX_HEALTH_REACHED));

                HeartItem heartItem = this.heartItem;
                if (heartItem != null && this.config.getProperty(HeartConfig.REWARD_HEART_ON_OVERLIMIT)) {
                    killer.getInventory().addItem(heartItem.getResult());
                }
            }
        }

        boolean takeHealthFromVictim = this.config.getProperty(BaseConfig.TAKE_HEALTH_FROM_VICTIM);

        double victimMaxHealth = this.adapter.getMaxHealth(victim);
        double victimNewHealth = victimMaxHealth - healthChange;

        int minimumHealth = this.config.getProperty(BaseConfig.MINIMUM_HEALTH);

        if (takeHealthFromVictim && victimNewHealth >= minimumHealth) {
            this.adapter.setMaxHealth(victim, victimNewHealth);
        }

        if (this.config.getProperty(BaseConfig.CUSTOM_ACTIONS).isEmpty()) {
            return;
        }

        this.config.getProperty(BaseConfig.CUSTOM_ACTIONS).forEach((actionKey, action) -> {
            if (!action.isEnabled()) {
                return;
            }

            if (victimMaxHealth - healthChange > action.getActivateAtHealth()) {
                return;
            }

            switch (action.getType()) {
                case SPECTATOR_MODE:
                    victim.setGameMode(GameMode.SPECTATOR);
                    break;
                case DISPATCH_COMMANDS:
                    action.getParameters().forEach(command -> {
                        command = this.formatPlaceholders(command, victim, killer);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    });
                    break;
                case BROADCAST:
                    action.getParameters().stream()
                        .map(message -> this.formatPlaceholders(message, victim, killer))
                        .map(message -> StringUtils.replace(message, "{player}", victim.getName()))
                        .map(GreatLifeStealPlugin::formatColor)
                        .forEach(Bukkit::broadcastMessage);
                    break;
                default:
                    throw new IllegalArgumentException("Case for the specified action does not exist.");
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
