package io.github.zrdzn.minecraft.greatlifesteal.elimination;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ch.jalu.configme.SettingsManager;
import io.github.zrdzn.minecraft.greatlifesteal.action.ActionType;
import io.github.zrdzn.minecraft.greatlifesteal.config.BaseConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.DisabledWorldsConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.bean.ActionBean;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EliminationJoinPreventListener implements Listener {

    private final Logger logger = LoggerFactory.getLogger(EliminationJoinPreventListener.class);

    private final Plugin plugin;
    private final BukkitScheduler scheduler;
    private final SettingsManager config;
    private final EliminationFacade eliminationFacade;
    private final EliminationRemovalCache eliminationRemovalCache;

    public EliminationJoinPreventListener(Plugin plugin, SettingsManager config, EliminationFacade eliminationFacade,
                                          EliminationRemovalCache eliminationRemovalCache) {
        this.plugin = plugin;
        this.scheduler = plugin.getServer().getScheduler();
        this.config = config;
        this.eliminationFacade = eliminationFacade;
        this.eliminationRemovalCache = eliminationRemovalCache;
    }

    @EventHandler
    public void preventFromJoining(AsyncPlayerPreLoginEvent event) {
        UUID playerUuid = event.getUniqueId();

        this.scheduler.runTaskAsynchronously(this.plugin, () -> {
            Elimination elimination;
            try {
                Optional<Elimination> eliminationMaybe = this.eliminationFacade.findEliminationByPlayerUuid(playerUuid);
                if (!eliminationMaybe.isPresent()) {
                    return;
                }

                elimination = eliminationMaybe.get();
            } catch (EliminationException exception) {
                this.logger.error("An error occurred while finding an elimination by player's unique id.", exception);
                return;
            }

            ActionBean action = this.config.getProperty(BaseConfig.CUSTOM_ACTIONS).get(elimination.getAction());
            if (action == null || !action.isEnabled()) {
                return;
            }

            List<String> disabledWorlds = this.config.getProperty(DisabledWorldsConfig.ELIMINATIONS);

            if (disabledWorlds.contains(elimination.getLastWorld())) {
                return;
            }

            if (action.getType() == ActionType.BROADCAST) {
                return;
            }

            // Kick player if he is not revived.
            this.scheduler.runTask(this.plugin, () -> {
                if (elimination.getRevive() != EliminationReviveStatus.COMPLETED) {
                    if (action.getType() == ActionType.BAN) {
                        String reason = ChatColor.translateAlternateColorCodes('&', String.join("\n", action.getParameters()));
                        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, reason);
                    }

                    return;
                }

                this.eliminationRemovalCache.addPlayer(playerUuid);
            });
        });
    }

}
