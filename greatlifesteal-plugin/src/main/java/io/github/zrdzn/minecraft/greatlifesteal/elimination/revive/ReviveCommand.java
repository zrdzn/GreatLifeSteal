package io.github.zrdzn.minecraft.greatlifesteal.elimination.revive;

import java.util.Optional;
import io.github.zrdzn.minecraft.greatlifesteal.PluginConfig;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.Elimination;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationException;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationFacade;
import io.github.zrdzn.minecraft.greatlifesteal.message.MessageFacade;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReviveCommand implements CommandExecutor {

    private final Logger logger = LoggerFactory.getLogger(ReviveCommand.class);

    private final Plugin plugin;
    private final PluginConfig config;
    private final EliminationFacade eliminationFacade;

    public ReviveCommand(Plugin plugin, PluginConfig config, EliminationFacade eliminationFacade) {
        this.plugin = plugin;
        this.config = config;
        this.eliminationFacade = eliminationFacade;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("greatlifesteal.command.revive")) {
            MessageFacade.send(sender, this.config.getMessages().getNoPermissions());
            return true;
        }

        // /revive
        if (args.length == 0) {
            MessageFacade.send(sender, this.config.getMessages().getNoEliminationSpecified());
            return true;
        }

        String reviveKey = args[0];

        ReviveConfig revive = this.config.getRevives().get(reviveKey);
        if (revive == null) {
            MessageFacade.send(sender, this.config.getMessages().getReviveDoesNotExist());
            return true;
        }

        // /revive <elimination>
        if (args.length == 1) {
            MessageFacade.send(sender, this.config.getMessages().getPlayerIsInvalid());
            return true;
        }

        String victimName = args[1];

        BukkitScheduler scheduler = this.plugin.getServer().getScheduler();

        scheduler.runTaskAsynchronously(this.plugin, () -> {
            try {
                Optional<Elimination> eliminationMaybe = this.eliminationFacade.findEliminationByPlayerName(victimName);
                if (!eliminationMaybe.isPresent()) {
                    MessageFacade.send(sender, this.config.getMessages().getPlayerIsNotEliminated(), "{PLAYER}", victimName);
                    return;
                }

                // Execute all revive-related commands but do not remove the elimination from the database yet.
                boolean statusChanged = this.eliminationFacade.updateReviveByPlayerName(victimName, ReviveStatus.COMPLETED);
                if (statusChanged) {
                    scheduler.runTask(this.plugin, () -> {
                        this.logger.info("Executing initial revive commands for {}.", victimName);

                        revive.getCommands().getInitial().forEach(reviveCommand -> {
                            reviveCommand = MessageFacade.formatPlaceholders(reviveCommand, "{victim}", victimName);
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), reviveCommand);
                        });
                    });

                    return;
                }

                MessageFacade.send(sender, this.config.getMessages().getPlayerIsAlreadyRevived(), "{PLAYER}", victimName);
            } catch (EliminationException exception) {
                this.logger.error("Could not find or update an elimination.", exception);
                MessageFacade.send(sender, this.config.getMessages().getCouldNotRevive());
            }
        });

        return true;
    }

}
