package io.github.zrdzn.minecraft.greatlifesteal.elimination.revive;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import io.github.zrdzn.minecraft.greatlifesteal.PluginConfig;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.Elimination;
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

    public ReviveRestoreHealthListener(Plugin plugin, PluginConfig config, EliminationFacade eliminationFacade,
                                       DamageableAdapter damageableAdapter) {
        this.plugin = plugin;
        this.scheduler = plugin.getServer().getScheduler();
        this.config = config;
        this.eliminationFacade = eliminationFacade;
        this.damageableAdapter = damageableAdapter;
    }

    @EventHandler
    public void restorePlayerHealth(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUuid = player.getUniqueId();

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

            if (elimination.getRevive() != ReviveStatus.COMPLETED) {
                return;
            }

            // Remove elimination from database.
            try {
                this.eliminationFacade.removeEliminationByPlayerUuid(playerUuid);
            } catch (EliminationException exception) {
                this.logger.error("An error occurred while removing an elimination by player's unique id.", exception);
                MessageFacade.send(player, this.config.getMessages().getHealthSet());
                return;
            }

            ReviveConfig revive = this.config.getRevives().get(elimination.getEliminationKey());

            this.scheduler.runTask(this.plugin, () -> {
                // Perform post-revive commands.
                this.logger.info("Executing post-revive commands for player {}.", player.getName());
                revive.getCommands().getAfter().forEach(reviveCommand -> {
                    reviveCommand = MessageFacade.formatPlaceholders(reviveCommand, "{victim}", player.getName());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), reviveCommand);
                });

                // Set default health.
                this.damageableAdapter.setMaxHealth(player, this.config.getHealth().getDefaultMaximumHealth());
                this.logger.info("Set default maximum health for player {}.", player.getName());
            });
        });
    }

}
