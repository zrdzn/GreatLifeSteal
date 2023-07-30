package io.github.zrdzn.minecraft.greatlifesteal.elimination;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import io.github.zrdzn.minecraft.greatlifesteal.PluginConfig;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.revive.ReviveAwaitingQueue;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.revive.ReviveStatus;
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
    private final PluginConfig config;
    private final EliminationFacade eliminationFacade;
    private final ReviveAwaitingQueue reviveAwaitingQueue;

    public EliminationJoinPreventListener(Plugin plugin, PluginConfig config, EliminationFacade eliminationFacade,
                                          ReviveAwaitingQueue reviveAwaitingQueue) {
        this.plugin = plugin;
        this.scheduler = plugin.getServer().getScheduler();
        this.config = config;
        this.eliminationFacade = eliminationFacade;
        this.reviveAwaitingQueue = reviveAwaitingQueue;
    }

    @EventHandler
    public void checkIfPlayerIsRevived(AsyncPlayerPreLoginEvent event) {
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

            List<String> disabledWorlds = this.config.getDisabledWorlds().getEliminations();

            if (disabledWorlds.contains(elimination.getLastWorld())) {
                return;
            }

            this.scheduler.runTask(this.plugin, () -> {
                if (elimination.getRevive() == ReviveStatus.COMPLETED) {
                    this.reviveAwaitingQueue.addPlayer(playerUuid, elimination.getEliminationKey());
                }
            });
        });
    }

}
