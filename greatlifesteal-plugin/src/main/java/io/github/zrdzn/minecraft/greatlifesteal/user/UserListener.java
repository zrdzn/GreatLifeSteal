package io.github.zrdzn.minecraft.greatlifesteal.user;

import ch.jalu.configme.SettingsManager;
import io.github.zrdzn.minecraft.greatlifesteal.GreatLifeStealPlugin;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.BaseConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.DisabledWorldsConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.HealthChangeConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.MessagesConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.StealCooldownConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.heart.HeartDropConfig;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.Elimination;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationReviveStatus;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationService;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartItem;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartService;
import io.github.zrdzn.minecraft.greatlifesteal.message.MessageService;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

public class UserListener implements Listener {

    private final Map<Player, Entry<Player, Instant>> stealCooldowns = new HashMap<>();

    private final JavaPlugin plugin;
    private final Logger logger;
    private final SettingsManager config;
    private final EliminationService eliminationService;
    private final DamageableAdapter adapter;
    private final HeartService heartService;
    private final HeartItem heartItem;

    public UserListener(JavaPlugin plugin, Logger logger, SettingsManager config, EliminationService eliminationService,
                        DamageableAdapter adapter, HeartService heartService, HeartItem heartItem) {
        this.plugin = plugin;
        this.logger = logger;
        this.config = config;
        this.eliminationService = eliminationService;
        this.adapter = adapter;
        this.heartService = heartService;
        this.heartItem = heartItem;
    }

    @EventHandler
    public void setDefaultHearts(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            this.adapter.setMaxHealth(player, this.config.getProperty(BaseConfig.DEFAULT_HEALTH));
        }
    }

    @EventHandler
    public void changePlayerHearts(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        List<String> disabledWorlds = this.config.getProperty(DisabledWorldsConfig.HEALTH_CHANGE);
        if (disabledWorlds.contains(victim.getWorld().getName())) {
            return;
        }

        EntityDamageEvent lastDamageCause = victim.getLastDamageCause();

        boolean killByPlayerOnly = this.config.getProperty(BaseConfig.KILL_BY_PLAYER_ONLY);

        if (killer == null) {
            if (killByPlayerOnly) {
                return;
            }
        } else {
            if (disabledWorlds.contains(killer.getWorld().getName())) {
                return;
            }
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

            HeartItem heartItem = this.heartItem;

            if (victimNewHealth >= minimumHealth) {
                double killerNewHealth = killerMaxHealth + killerHealthChange;
                if (killerNewHealth <= this.config.getProperty(BaseConfig.MAXIMUM_HEALTH)) {
                    // Increase the killer's maximum health or give him the heart item.
                    if (this.config.getProperty(HeartDropConfig.ON_EVERY_KILL) && heartItem != null) {
                        this.heartService.giveHeartToPlayer(killer);
                    } else {
                        this.adapter.setMaxHealth(killer, killerNewHealth);
                    }
                } else {
                    MessageService.send(killer, this.config.getProperty(MessagesConfig.MAX_HEALTH_REACHED));

                    // Give the heart item to the killer if it is enabled.
                    if (heartItem != null && this.config.getProperty(HeartDropConfig.ON_LIMIT_EXCEED)) {
                        this.heartService.giveHeartToPlayer(killer);
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
                    case BAN:
                        Elimination elimination = new Elimination();
                        elimination.setCreatedAt(Instant.now());
                        elimination.setPlayerUuid(victim.getUniqueId());
                        elimination.setPlayerName(victimName);
                        elimination.setAction(actionKey);
                        elimination.setRevive(EliminationReviveStatus.PENDING);

                        this.eliminationService.createElimination(elimination).join()
                                .peek(ignored -> victim.kickPlayer(ChatColor.translateAlternateColorCodes('&', String.join("\n", action.getParameters()))))
                                .onError(error -> this.logger.error("Could not eliminate a player.", error));

                        break;
                    default:
                        throw new IllegalArgumentException("Case for the specified action does not exist.");
                }
            }, action.getDelay());
        });
    }

}
