package io.github.zrdzn.minecraft.greatlifesteal.user;

import ch.jalu.configme.SettingsManager;
import io.github.zrdzn.minecraft.greatlifesteal.GreatLifeStealPlugin;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.BaseConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.HealthChangeConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.MessagesConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.StealCooldownConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.heart.HeartConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.heart.HeartDropConfig;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationReviveStatus;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationService;
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
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

public class UserListener implements Listener {

    private final Map<Player, Entry<Player, Instant>> stealCooldowns = new HashMap<>();

    private final JavaPlugin plugin;
    private final Logger logger;
    private final SettingsManager config;
    private final EliminationService eliminationService;
    private final DamageableAdapter adapter;
    private final HealthCache cache;
    private final HeartItem heartItem;
    private final boolean latestVersion;

    public UserListener(JavaPlugin plugin, Logger logger, SettingsManager config, EliminationService eliminationService,
                        DamageableAdapter adapter, HealthCache cache, HeartItem heartItem, boolean latestVersion) {
        this.plugin = plugin;
        this.logger = logger;
        this.config = config;
        this.eliminationService = eliminationService;
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
            return;
        }

        UUID playerUuid = player.getUniqueId();

        this.eliminationService.getElimination(playerUuid).thenAccept(result -> result
                .peek(eliminationMaybe -> {
                    if (!eliminationMaybe.isPresent()) {
                        return;
                    }

                    if (eliminationMaybe.get().getRevive() != EliminationReviveStatus.COMPLETED) {
                        return;
                    }

                    this.eliminationService.removeElimination(playerUuid).join()
                            .peek(ignored -> {
                                MessageService.send(player, this.config.getProperty(MessagesConfig.SUCCESS_DEFAULT_HEALTH_SET));
                                this.adapter.setMaxHealth(player, this.config.getProperty(BaseConfig.DEFAULT_HEALTH));
                            })
                            .onError(error -> {
                                this.logger.error("Could not set a default health.", error);
                                MessageService.send(player, this.config.getProperty(MessagesConfig.FAIL_DEFAULT_HEALTH_SET));
                            });
                })
                .onError(error -> {
                    this.logger.error("Could not get an elimination.", error);
                    MessageService.send(player, this.config.getProperty(MessagesConfig.FAIL_DEFAULT_HEALTH_SET));
                }));

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

        EntityDamageEvent lastDamageCause = victim.getLastDamageCause();

        boolean killByPlayerOnly = this.config.getProperty(BaseConfig.KILL_BY_PLAYER_ONLY);

        if (killByPlayerOnly && killer == null) {
            return;
        }

        double victimHealthChange = this.config.getProperty(HealthChangeConfig.VICTIM);
        if (victimHealthChange <= 0.0D) {
            return;
        }

        double victimMaxHealth = this.adapter.getMaxHealth(victim);
        double victimNewHealth = victimMaxHealth - victimHealthChange;

        double minimumHealth = this.config.getProperty(BaseConfig.MINIMUM_HEALTH);

        String killerName = null;
        String formattedKillerMaxHealth = null;

        double killerHealthChange = this.config.getProperty(HealthChangeConfig.KILLER);
        if (killerHealthChange > 0.0D && killer != null) {
            killerName = killer.getName();

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

            double killerMaxHealth = this.adapter.getMaxHealth(killer);
            formattedKillerMaxHealth = String.valueOf((int) killerMaxHealth);

            Inventory killerInventory = killer.getInventory();
            HeartItem heartItem = this.heartItem;

            if (victimNewHealth >= minimumHealth) {
                double killerNewHealth = killerMaxHealth + killerHealthChange;
                if (killerNewHealth <= this.config.getProperty(BaseConfig.MAXIMUM_HEALTH)) {
                    // Increase the killer's maximum health or give him the heart item.
                    if (this.config.getProperty(HeartDropConfig.ON_EVERY_KILL) && heartItem != null) {
                        if (this.config.getProperty(HeartConfig.DROP_ON_GROUND)) {
                            killer.getWorld().dropItemNaturally(killer.getEyeLocation(), heartItem.result);
                        } else {
                            killerInventory.addItem(heartItem.result);
                        }
                    } else {
                        this.adapter.setMaxHealth(killer, killerNewHealth);
                    }
                } else {
                    MessageService.send(killer, this.config.getProperty(MessagesConfig.MAX_HEALTH_REACHED));

                    // Give the heart item to the killer if it is enabled.
                    if (heartItem != null && this.config.getProperty(HeartDropConfig.ON_LIMIT_EXCEED)) {
                        if (this.config.getProperty(HeartConfig.DROP_ON_GROUND)) {
                            killer.getWorld().dropItemNaturally(killer.getEyeLocation(), heartItem.result);
                        } else {
                            killerInventory.addItem(heartItem.result);
                        }
                    }
                }
            }
        }

        if (victimNewHealth >= minimumHealth) {
            this.adapter.setMaxHealth(victim, victimNewHealth);
        }

        if (this.config.getProperty(BaseConfig.CUSTOM_ACTIONS).isEmpty()) {
            return;
        }

        if (killerName == null && lastDamageCause != null) {
            if (lastDamageCause instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent damage = (EntityDamageByEntityEvent) lastDamageCause;
                killerName = damage.getDamager().getName();
            } else {
                killerName = lastDamageCause.getCause().name();
            }

            formattedKillerMaxHealth = "N/A";
        }

        String victimName = victim.getName();

        String[] placeholders = {
                "{player}", victimName,
                "{victim}", victimName,
                "{killer}", killerName,
                "{victim_max_health}", String.valueOf((int) victim.getMaxHealth()),
                "{killer_max_health}", formattedKillerMaxHealth,
        };

        this.config.getProperty(BaseConfig.CUSTOM_ACTIONS).forEach((actionKey, action) -> {
            if (!action.isEnabled()) {
                return;
            }

            if (victimNewHealth > action.getActivateAtHealth()) {
                return;
            }

            if (action.getDelay() < 0L) {
                return;
            }

            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                switch (action.getType()) {
                    case SPECTATOR_MODE:
                        victim.setGameMode(GameMode.SPECTATOR);
                        break;
                    case DISPATCH_COMMANDS:
                        action.getParameters().forEach(command -> {
                            command = MessageService.formatPlaceholders(command, placeholders);
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                        });
                        break;
                    case BROADCAST:
                        action.getParameters().stream()
                                .map(message -> MessageService.formatPlaceholders(message, placeholders))
                                .map(GreatLifeStealPlugin::formatColor)
                                .forEach(Bukkit::broadcastMessage);
                        break;
                    default:
                        throw new IllegalArgumentException("Case for the specified action does not exist.");
                }
            }, action.getDelay());
        });
    }

}
