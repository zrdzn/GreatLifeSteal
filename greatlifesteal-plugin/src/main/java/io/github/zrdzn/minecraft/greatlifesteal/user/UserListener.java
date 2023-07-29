package io.github.zrdzn.minecraft.greatlifesteal.user;

import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import io.github.zrdzn.minecraft.greatlifesteal.GreatLifeStealPlugin;
import io.github.zrdzn.minecraft.greatlifesteal.PluginConfig;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationException;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationFacade;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartFacade;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartItem;
import io.github.zrdzn.minecraft.greatlifesteal.message.MessageFacade;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserListener implements Listener {

    private final Logger logger = LoggerFactory.getLogger(UserListener.class);

    private final Map<Player, Entry<Player, Instant>> stealCooldowns = new HashMap<>();

    private final JavaPlugin plugin;
    private final PluginConfig config;
    private final EliminationFacade eliminationFacade;
    private final DamageableAdapter adapter;
    private final HeartFacade heartFacade;
    private final HeartItem heartItem;

    public UserListener(JavaPlugin plugin, PluginConfig config, EliminationFacade eliminationFacade,
                        DamageableAdapter adapter, HeartFacade heartFacade, HeartItem heartItem) {
        this.plugin = plugin;
        this.config = config;
        this.eliminationFacade = eliminationFacade;
        this.adapter = adapter;
        this.heartFacade = heartFacade;
        this.heartItem = heartItem;
    }

    @EventHandler
    public void setDefaultHearts(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            this.adapter.setMaxHealth(player, this.config.getHealth().getDefaultMaximumHealth());
        }
    }

    @EventHandler
    public void changePlayerHearts(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        List<String> disabledWorlds = this.config.getDisabledWorlds().getHealthChange();
        if (disabledWorlds.contains(victim.getWorld().getName())) {
            return;
        }

        EntityDamageEvent lastDamageCause = victim.getLastDamageCause();

        if (killer == null) {
            if (this.config.isKillByPlayerOnly()) {
                return;
            }
        } else {
            if (disabledWorlds.contains(killer.getWorld().getName())) {
                return;
            }
        }

        double victimHealthChange = this.config.getHealth().getChange().getVictim();
        if (victimHealthChange <= 0.0D) {
            return;
        }

        double victimMaxHealth = this.adapter.getMaxHealth(victim);
        double victimNewHealth = victimMaxHealth - victimHealthChange;

        double minimumHealth = this.config.getHealth().getMinimumHealth();

        String killerName = null;
        String formattedKillerMaxHealth = null;

        double killerHealthChange = this.config.getHealth().getChange().getKiller();
        if (killerHealthChange > 0.0D && killer != null) {
            killerName = killer.getName();

            if (this.config.isIgnoreSameIp()) {
                if (victim.getAddress().getAddress().equals(killer.getAddress().getAddress())) {
                    return;
                }
            }

            // Checks if there is any cooldown active on the killer.
            if (this.config.getStealCooldown().isEnabled()) {
                Entry<Player, Instant> cooldownEntry = this.stealCooldowns.get(killer);
                if (cooldownEntry != null && victim.equals(cooldownEntry.getKey())) {
                    Duration difference = Duration.between(cooldownEntry.getValue(), Instant.now());
                    Duration limit = Duration.ofSeconds(this.config.getStealCooldown().getCooldown());
                    if (difference.compareTo(limit) < 0) {
                        String[] placeholders = { "{AMOUNT}", String.valueOf(limit.getSeconds() - difference.getSeconds()) };
                        MessageFacade.send(killer, this.config.getMessages().getActiveCooldown(), placeholders);
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
                if (killerNewHealth <= this.config.getHealth().getMaximumHealth()) {
                    // Increase the killer's maximum health or give him the heart item.
                    if (this.config.getHeart().getDrop().isOnEveryKill() && heartItem != null) {
                        this.heartFacade.giveHeartToPlayer(killer);
                    } else {
                        this.adapter.setMaxHealth(killer, killerNewHealth);
                    }
                } else {
                    MessageFacade.send(killer, this.config.getMessages().getMaxHealthReached());

                    // Give the heart item to the killer if it is enabled.
                    if (heartItem != null && this.config.getHeart().getDrop().isOnLimitExceed()) {
                        this.heartFacade.giveHeartToPlayer(killer);
                    }
                }
            }
        }

        if (victimNewHealth >= minimumHealth) {
            this.adapter.setMaxHealth(victim, victimNewHealth);
        }

        if (this.config.getActions().isEmpty()) {
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

        this.config.getActions().forEach((actionKey, action) -> {
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
                    case DISPATCH_COMMANDS:
                        action.getParameters().forEach(command -> {
                            command = MessageFacade.formatPlaceholders(command, placeholders);
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                        });
                        break;
                    case BROADCAST:
                        action.getParameters().stream()
                                .map(message -> MessageFacade.formatPlaceholders(message, placeholders))
                                .map(GreatLifeStealPlugin::formatColor)
                                .forEach(Bukkit::broadcastMessage);
                        break;
                    case BAN:
                        BukkitScheduler scheduler = this.plugin.getServer().getScheduler();

                        scheduler.runTaskAsynchronously(this.plugin, () -> {
                            try {
                                this.eliminationFacade.createElimination(victim.getUniqueId(), victimName, actionKey, victim.getWorld().getName());
                                scheduler.runTask(this.plugin, () ->
                                        victim.kickPlayer(ChatColor.translateAlternateColorCodes('&', String.join("\n", action.getParameters())))
                                );
                            } catch (EliminationException exception) {
                                this.logger.error("Could not eliminate a player.", exception);
                                MessageFacade.send(victim, this.config.getMessages().getCouldNotEliminateSelf());
                            }
                        });
                        break;
                    default:
                        throw new IllegalArgumentException("Case for the specified action does not exist.");
                }
            }, action.getDelay());
        });
    }

}
