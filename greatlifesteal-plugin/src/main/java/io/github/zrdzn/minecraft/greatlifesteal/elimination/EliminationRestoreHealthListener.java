package io.github.zrdzn.minecraft.greatlifesteal.elimination;

import java.util.UUID;
import io.github.zrdzn.minecraft.greatlifesteal.PluginConfig;
import io.github.zrdzn.minecraft.greatlifesteal.message.MessageFacade;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EliminationRestoreHealthListener implements Listener {

    private final Logger logger = LoggerFactory.getLogger(EliminationRestoreHealthListener.class);

    private final Plugin plugin;
    private final BukkitScheduler scheduler;
    private final PluginConfig config;
    private final EliminationFacade eliminationFacade;
    private final DamageableAdapter damageableAdapter;
    private final EliminationRemovalCache eliminationRemovalCache;

    public EliminationRestoreHealthListener(Plugin plugin, PluginConfig config, EliminationFacade eliminationFacade,
                                            DamageableAdapter damageableAdapter,
                                            EliminationRemovalCache eliminationRemovalCache) {
        this.plugin = plugin;
        this.scheduler = plugin.getServer().getScheduler();
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
            this.scheduler.runTaskAsynchronously(this.plugin, () -> {
                try {
                    this.eliminationFacade.removeEliminationByPlayerUuid(playerUuid);
                } catch (EliminationException exception) {
                    this.logger.error("An error occurred while removing an elimination by player's unique id.", exception);
                    MessageFacade.send(player, this.config.getMessages().getHealthSet());
                    return;
                }

                this.scheduler.runTask(this.plugin, () -> {
                    MessageFacade.send(player, this.config.getMessages().getRevivedSuccessfully());
                    this.damageableAdapter.setMaxHealth(player, this.config.getHealth().getDefaultMaximumHealth());
                    this.eliminationRemovalCache.removePlayer(playerUuid);
                });
            });
        }
    }

}
