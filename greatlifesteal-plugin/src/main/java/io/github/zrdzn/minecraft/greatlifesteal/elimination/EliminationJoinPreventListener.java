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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import panda.std.Result;

public class EliminationJoinPreventListener implements Listener {

    private final Logger logger = LoggerFactory.getLogger(EliminationJoinPreventListener.class);

    private final SettingsManager config;
    private final EliminationFacade eliminationFacade;
    private final EliminationRemovalCache eliminationRemovalCache;

    public EliminationJoinPreventListener(SettingsManager config, EliminationFacade eliminationFacade,
                                          EliminationRemovalCache eliminationRemovalCache) {
        this.config = config;
        this.eliminationFacade = eliminationFacade;
        this.eliminationRemovalCache = eliminationRemovalCache;
    }

    @EventHandler
    public void preventFromJoining(AsyncPlayerPreLoginEvent event) {
        UUID playerUuid = event.getUniqueId();

        Result<Optional<Elimination>, Exception> foundElimination = this.eliminationFacade.getElimination(playerUuid).join();

        List<String> disabledWorlds = this.config.getProperty(DisabledWorldsConfig.ELIMINATIONS);

        foundElimination
                .peek(eliminationMaybe -> {
                    if (!eliminationMaybe.isPresent()) {
                        return;
                    }

                    Elimination elimination = eliminationMaybe.get();

                    ActionBean action = this.config.getProperty(BaseConfig.CUSTOM_ACTIONS).get(elimination.getAction());
                    if (action == null || !action.isEnabled()) {
                        return;
                    }

                    if (disabledWorlds.contains(elimination.getLastWorld())) {
                        return;
                    }

                    if (action.getType() == ActionType.BROADCAST) {
                        return;
                    }

                    // Kick player if he is not revived.
                    if (elimination.getRevive() != EliminationReviveStatus.COMPLETED) {
                        if (action.getType() == ActionType.BAN) {
                            String reason = ChatColor.translateAlternateColorCodes('&', String.join("\n", action.getParameters()));
                            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, reason);
                        }

                        return;
                    }

                    this.eliminationRemovalCache.addPlayer(playerUuid);
                })
                .onError(error -> this.logger.error("Could not get an elimination.", error));
    }

}
