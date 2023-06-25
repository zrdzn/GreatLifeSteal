package io.github.zrdzn.minecraft.greatlifesteal.elimination;

import java.util.UUID;
import ch.jalu.configme.SettingsManager;
import io.github.zrdzn.minecraft.greatlifesteal.config.BaseConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.MessagesConfig;
import io.github.zrdzn.minecraft.greatlifesteal.message.MessageFacade;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.slf4j.Logger;

public class EliminationRestoreHealthListener implements Listener {

    private final Logger logger;
    private final SettingsManager config;
    private final EliminationFacade eliminationFacade;
    private final DamageableAdapter damageableAdapter;
    private final EliminationRemovalCache eliminationRemovalCache;

    public EliminationRestoreHealthListener(Logger logger, SettingsManager config, EliminationFacade eliminationFacade,
                                            DamageableAdapter damageableAdapter, EliminationRemovalCache eliminationRemovalCache) {
        this.logger = logger;
        this.config = config;
        this.eliminationFacade = eliminationFacade;
        this.damageableAdapter = damageableAdapter;
        this.eliminationRemovalCache = eliminationRemovalCache;
    }

    @EventHandler
    public void setDefaultHearts(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUuid = player.getUniqueId();

        if (this.eliminationRemovalCache.isPlayerPresent(playerUuid)) {
            this.eliminationFacade.removeElimination(playerUuid).join()
                    .peek(ignored -> {
                        MessageFacade.send(player, this.config.getProperty(MessagesConfig.SUCCESS_DEFAULT_HEALTH_SET));
                        this.damageableAdapter.setMaxHealth(player, this.config.getProperty(BaseConfig.DEFAULT_HEALTH));
                        this.eliminationRemovalCache.removePlayer(playerUuid);
                    })
                    .onError(error -> {
                        this.logger.error("Could not remove an elimination.", error);
                        MessageFacade.send(player, this.config.getProperty(MessagesConfig.FAIL_DEFAULT_HEALTH_SET));
                    });
        }
    }

}
