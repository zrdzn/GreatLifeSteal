package io.github.zrdzn.minecraft.greatlifesteal.command;

import io.github.zrdzn.minecraft.greatlifesteal.GreatLifeStealPlugin;
import io.github.zrdzn.minecraft.greatlifesteal.configs.MessagesConfig;
import io.github.zrdzn.minecraft.greatlifesteal.message.MessageService;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LifeStealCommand implements CommandExecutor {

    private final GreatLifeStealPlugin plugin;
    private final MessagesConfig messages;
    private final DamageableAdapter adapter;
    private final Server server;

    public LifeStealCommand(GreatLifeStealPlugin plugin, MessagesConfig messages, DamageableAdapter adapter,
                            Server server) {
        this.plugin = plugin;
        this.messages = messages;
        this.adapter = adapter;
        this.server = server;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            MessageService.send(sender, this.messages.commandUsage);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "set":
                if (!sender.hasPermission("greatlifesteal.command.set")) {
                    MessageService.send(sender, this.messages.noPermissions);
                    return true;
                }

                if (args.length < 3) {
                    MessageService.send(sender, this.messages.commandUsage);
                    return true;
                }

                Player target = this.server.getPlayer(args[1]);
                if (target == null) {
                    MessageService.send(sender, this.messages.invalidPlayerProvided);
                    return true;
                }

                int health;
                try {
                    health = Integer.parseInt(args[2]);
                } catch (NumberFormatException exception) {
                    MessageService.send(sender, this.messages.invalidHealthProvided);
                    return true;
                }

                this.adapter.setMaxHealth(target, health);

                String[] placeholders = { "{PLAYER}", target.getDisplayName(), "{HEALTH}", String.valueOf(health) };
                MessageService.send(sender, this.messages.successfulCommandSet, placeholders);

                break;
            case "reload":
                if (!sender.hasPermission("greatlifesteal.command.reload")) {
                    MessageService.send(sender, this.messages.noPermissions);
                    return true;
                }

                if (this.plugin.loadConfigurations()) {
                    MessageService.send(sender, this.messages.successfulCommandReload);
                    return true;
                }

                MessageService.send(sender, this.messages.failCommandReload);

                break;
            default:
                MessageService.send(sender, this.messages.commandUsage);
        }

        return true;
    }

}
