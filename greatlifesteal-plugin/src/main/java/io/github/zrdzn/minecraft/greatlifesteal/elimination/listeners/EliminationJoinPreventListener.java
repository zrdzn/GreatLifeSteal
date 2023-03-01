package io.github.zrdzn.minecraft.greatlifesteal.elimination.listeners;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ch.jalu.configme.SettingsManager;
import io.github.zrdzn.minecraft.greatlifesteal.action.ActionType;
import io.github.zrdzn.minecraft.greatlifesteal.config.bean.beans.ActionBean;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.BaseConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.DisabledWorldsConfig;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.Elimination;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationRemovalCache;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationReviveStatus;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationService;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.slf4j.Logger;
import panda.std.Result;

public class EliminationJoinPreventListener implements Listener {

    private final Logger logger;
    private final SettingsManager config;
    private final EliminationService eliminationService;
    private final EliminationRemovalCache eliminationRemovalCache;

    public EliminationJoinPreventListener(Logger logger, SettingsManager config, EliminationService eliminationService,
                                          EliminationRemovalCache eliminationRemovalCache) {
        this.logger = logger;
        this.config = config;
        this.eliminationService = eliminationService;
        this.eliminationRemovalCache = eliminationRemovalCache;
    }

    @EventHandler
    public void preventFromJoining(AsyncPlayerPreLoginEvent event) {
        UUID playerUuid = event.getUniqueId();

        Result<Optional<Elimination>, Exception> foundElimination = this.eliminationService.getElimination(playerUuid).join();

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
