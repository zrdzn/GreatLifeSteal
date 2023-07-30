package io.github.zrdzn.minecraft.greatlifesteal.elimination.revive;

import java.util.UUID;
import io.github.zrdzn.minecraft.greatlifesteal.PluginConfig;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationException;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationFacade;
import io.github.zrdzn.minecraft.greatlifesteal.message.MessageFacade;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReviveRestoreHealthListener implements Listener {

    private final Logger logger = LoggerFactory.getLogger(ReviveRestoreHealthListener.class);

    private final Plugin plugin;
    private final BukkitScheduler scheduler;
    private final PluginConfig config;
    private final EliminationFacade eliminationFacade;
    private final DamageableAdapter damageableAdapter;
    private final ReviveAwaitingQueue reviveAwaitingQueue;

    public ReviveRestoreHealthListener(Plugin plugin, PluginConfig config, EliminationFacade eliminationFacade,
                                       DamageableAdapter damageableAdapter,
                                       ReviveAwaitingQueue reviveAwaitingQueue) {
        this.plugin = plugin;
        this.scheduler = plugin.getServer().getScheduler();
        this.config = config;
        this.eliminationFacade = eliminationFacade;
        this.damageableAdapter = damageableAdapter;
        this.reviveAwaitingQueue = reviveAwaitingQueue;
    }

    @EventHandler
    public void restorePlayerHealth(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUuid = player.getUniqueId();

        if (!this.reviveAwaitingQueue.isPlayerPresent(playerUuid)) {
            return;
        }

        this.scheduler.runTaskAsynchronously(this.plugin, () -> {
            try {
                this.eliminationFacade.removeEliminationByPlayerUuid(playerUuid);
            } catch (EliminationException exception) {
                this.logger.error("An error occurred while removing an elimination by player's unique id.", exception);
                MessageFacade.send(player, this.config.getMessages().getHealthSet());
                return;
            }

            String reviveKey = this.reviveAwaitingQueue.getReviveKey(playerUuid);
            if (reviveKey == null) {
                MessageFacade.send(player, this.config.getMessages().getSomethingWentWrong());
                throw new IllegalStateException("Revive key is null.");
            }

            ReviveConfig revive = this.config.getRevives().get(reviveKey);

            this.scheduler.runTask(this.plugin, () -> {
                // Perform post-revive commands.
                revive.getCommands().getAfter().forEach(reviveCommand -> {
                    reviveCommand = MessageFacade.formatPlaceholders(reviveCommand, "{victim}", player.getName());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), reviveCommand);
                });

                // Set default health.
                this.damageableAdapter.setMaxHealth(player, this.config.getHealth().getDefaultMaximumHealth());

                this.reviveAwaitingQueue.removePlayer(playerUuid);
            });
        });
    }

}
