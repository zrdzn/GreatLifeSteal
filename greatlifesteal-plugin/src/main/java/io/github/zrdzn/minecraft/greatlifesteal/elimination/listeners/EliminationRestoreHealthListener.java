package io.github.zrdzn.minecraft.greatlifesteal.elimination.listeners;

import java.util.UUID;
import ch.jalu.configme.SettingsManager;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.BaseConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.MessagesConfig;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationRemovalCache;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationService;
import io.github.zrdzn.minecraft.greatlifesteal.message.MessageService;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.slf4j.Logger;

public class EliminationRestoreHealthListener implements Listener {

    private final Logger logger;
    private final SettingsManager config;
    private final EliminationService eliminationService;
    private final DamageableAdapter damageableAdapter;
    private final EliminationRemovalCache eliminationRemovalCache;

    public EliminationRestoreHealthListener(Logger logger, SettingsManager config, EliminationService eliminationService,
                                            DamageableAdapter damageableAdapter, EliminationRemovalCache eliminationRemovalCache) {
        this.logger = logger;
        this.config = config;
        this.eliminationService = eliminationService;
        this.damageableAdapter = damageableAdapter;
        this.eliminationRemovalCache = eliminationRemovalCache;
    }

    @EventHandler
    public void setDefaultHearts(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUuid = player.getUniqueId();

        if (this.eliminationRemovalCache.isPlayerPresent(playerUuid)) {
            this.eliminationService.removeElimination(playerUuid).join()
                    .peek(ignored -> {
                        MessageService.send(player, this.config.getProperty(MessagesConfig.SUCCESS_DEFAULT_HEALTH_SET));
                        this.damageableAdapter.setMaxHealth(player, this.config.getProperty(BaseConfig.DEFAULT_HEALTH));
                        this.eliminationRemovalCache.removePlayer(playerUuid);
                    })
                    .onError(error -> {
                        this.logger.error("Could not remove an elimination.", error);
                        MessageService.send(player, this.config.getProperty(MessagesConfig.FAIL_DEFAULT_HEALTH_SET));
                    });
        }
    }

}
