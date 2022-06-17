package io.github.zrdzn.minecraft.greatlifesteal.command;

import ch.jalu.configme.SettingsManager;
import io.github.zrdzn.minecraft.greatlifesteal.GreatLifeStealPlugin;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.BaseConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.EliminationConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.MessagesConfig;
import io.github.zrdzn.minecraft.greatlifesteal.message.MessageService;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LifeStealCommand implements CommandExecutor {

    private final GreatLifeStealPlugin plugin;
    private final SettingsManager config;
    private final DamageableAdapter adapter;
    private final Server server;

    public LifeStealCommand(GreatLifeStealPlugin plugin, SettingsManager config, DamageableAdapter adapter,
                            Server server) {
        this.plugin = plugin;
        this.config = config;
        this.adapter = adapter;
        this.server = server;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            MessageService.send(sender, this.config.getProperty(MessagesConfig.COMMAND_USAGE));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "set": {
                if (!sender.hasPermission("greatlifesteal.command.set")) {
                    MessageService.send(sender, this.config.getProperty(MessagesConfig.NO_PERMISSIONS));
                    return true;
                }

                if (args.length < 3) {
                    MessageService.send(sender, this.config.getProperty(MessagesConfig.COMMAND_USAGE));
                    return true;
                }

                Player target = this.server.getPlayer(args[1]);
                if (target == null) {
                    MessageService.send(sender, this.config.getProperty(MessagesConfig.INVALID_PLAYER_PROVIDED));
                    return true;
                }

                int health;
                try {
                    health = Integer.parseInt(args[2]);
                } catch (NumberFormatException exception) {
                    MessageService.send(sender, this.config.getProperty(MessagesConfig.INVALID_HEALTH_PROVIDED));
                    return true;
                }

                this.adapter.setMaxHealth(target, health);

                String[] placeholders = { "{PLAYER}", target.getDisplayName(), "{HEALTH}", String.valueOf(health) };
                MessageService.send(sender, this.config.getProperty(MessagesConfig.SUCCESSFUL_COMMAND_SET), placeholders);

                break;
            }
            case "reload":
                if (!sender.hasPermission("greatlifesteal.command.reload")) {
                    MessageService.send(sender, this.config.getProperty(MessagesConfig.NO_PERMISSIONS));
                    return true;
                }

                if (this.plugin.loadConfigurations()) {
                    MessageService.send(sender, this.config.getProperty(MessagesConfig.SUCCESSFUL_COMMAND_RELOAD));
                    return true;
                }

                MessageService.send(sender, this.config.getProperty(MessagesConfig.FAIL_COMMAND_RELOAD));

                break;
            case "lives": {
                if (!this.config.getProperty(EliminationConfig.ENABLED)) {
                    MessageService.send(sender, this.config.getProperty(MessagesConfig.ELIMINATION_NOT_ENABLED));
                    return true;
                }

                Player target;
                if (args.length == 1) {
                    if (!sender.hasPermission("greatlifesteal.command.lives.self") &&
                            !sender.hasPermission("greatlifesteal.command.lives")) {
                        MessageService.send(sender, this.config.getProperty(MessagesConfig.NO_PERMISSIONS));
                        return true;
                    }

                    target = (Player) sender;
                } else {
                    if (!sender.hasPermission("greatlifesteal.command.lives")) {
                        MessageService.send(sender, this.config.getProperty(MessagesConfig.NO_PERMISSIONS));
                        return true;
                    }

                    target = this.server.getPlayer(args[1]);
                    if (target == null) {
                        MessageService.send(sender, this.config.getProperty(MessagesConfig.INVALID_PLAYER_PROVIDED));
                        return true;
                    }
                }

                int requiredHealth = this.config.getProperty(EliminationConfig.REQUIRED_HEALTH);
                double playerHealth = this.adapter.getMaxHealth(target);
                int lives = 0;

                if (playerHealth > requiredHealth) {
                    int healthChange = this.config.getProperty(BaseConfig.HEALTH_CHANGE);
                    lives = (int) Math.ceil((playerHealth - requiredHealth) / healthChange);
                }

                String[] placeholders = { "{PLAYER}", target.getName(), "{LIVES}", String.valueOf(lives) };
                MessageService.send(sender, this.config.getProperty(MessagesConfig.SUCCESSFUL_COMMAND_LIVES), placeholders);

                break;
            }
            default:
                MessageService.send(sender, this.config.getProperty(MessagesConfig.COMMAND_USAGE));
        }

        return true;
    }

}
