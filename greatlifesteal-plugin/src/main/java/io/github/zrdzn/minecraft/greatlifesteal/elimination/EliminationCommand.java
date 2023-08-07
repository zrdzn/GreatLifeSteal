package io.github.zrdzn.minecraft.greatlifesteal.elimination;

import java.util.Optional;
import io.github.zrdzn.minecraft.greatlifesteal.PluginConfig;
import io.github.zrdzn.minecraft.greatlifesteal.message.MessageFacade;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EliminationCommand implements CommandExecutor {

    private final Logger logger = LoggerFactory.getLogger(EliminationCommand.class);

    private final Plugin plugin;
    private final PluginConfig config;
    private final EliminationFacade eliminationFacade;

    public EliminationCommand(Plugin plugin, PluginConfig config, EliminationFacade eliminationFacade) {
        this.plugin = plugin;
        this.config = config;
        this.eliminationFacade = eliminationFacade;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("greatlifesteal.command.eliminate")) {
            MessageFacade.send(sender, this.config.getMessages().getNoPermissions());
            return true;
        }

        // /eliminate
        if (args.length == 0) {
            MessageFacade.send(sender, this.config.getMessages().getNoEliminationSpecified());
            return true;
        }

        String eliminationKey = args[0];

        EliminationConfig eliminationConfig = this.config.getEliminations().get(eliminationKey);
        if (eliminationConfig == null) {
            MessageFacade.send(sender, this.config.getMessages().getEliminationDoesNotExist());
            return true;
        }

        // /eliminate <elimination>
        if (args.length == 1) {
            MessageFacade.send(sender, this.config.getMessages().getPlayerIsInvalid());
            return true;
        }

        Player victim = this.plugin.getServer().getPlayer(args[1]);
        if (victim == null) {
            MessageFacade.send(sender, this.config.getMessages().getPlayerIsInvalid());
            return true;
        }

        int senderHealth = 0;
        if (sender instanceof Player) {
            senderHealth = (int) ((Player) sender).getMaxHealth();
        }

        String victimName = victim.getName();

        String[] placeholders = {
                "{player}", victimName,
                "{victim}", victimName,
                "{killer}", sender.getName(),
                "{victim_max_health}", String.valueOf((int) victim.getMaxHealth()),
                "{killer_max_health}", String.valueOf(senderHealth),
        };

        BukkitScheduler scheduler = this.plugin.getServer().getScheduler();

        scheduler.runTaskAsynchronously(this.plugin, () -> {
            try {
                Optional<Elimination> eliminationMaybe = this.eliminationFacade.findEliminationByPlayerUuid(victim.getUniqueId());
                if (eliminationMaybe.isPresent()) {
                    MessageFacade.send(sender, this.config.getMessages().getPlayerIsAlreadyEliminated(), "{PLAYER}", victim.getName());
                    return;
                }

                this.eliminationFacade.createElimination(victim.getUniqueId(), victimName, eliminationKey, victim.getWorld().getName());
            } catch (EliminationException exception) {
                this.logger.error("Could not find or create an elimination.", exception);
                MessageFacade.send(sender, this.config.getMessages().getCouldNotEliminate());
                return;
            }

            scheduler.runTask(this.plugin, () ->
                    eliminationConfig.getCommands().forEach(eliminationCommand -> {
                        eliminationCommand = MessageFacade.formatPlaceholders(eliminationCommand, placeholders);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), eliminationCommand);
                    })
            );
        });

        return true;
    }

}
