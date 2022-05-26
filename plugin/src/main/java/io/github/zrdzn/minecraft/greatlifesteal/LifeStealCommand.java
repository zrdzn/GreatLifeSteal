package io.github.zrdzn.minecraft.greatlifesteal;

import io.github.zrdzn.minecraft.greatlifesteal.message.MessageService;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LifeStealCommand implements CommandExecutor {

    private final MessageService messageService;
    private final DamageableAdapter adapter;
    private final Server server;

    public LifeStealCommand(MessageService messageService, DamageableAdapter adapter, Server server) {
        this.messageService = messageService;
        this.adapter = adapter;
        this.server = server;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            this.messageService.send(sender, "commandUsage");
            return true;
        }

        if (!sender.hasPermission("greatlifesteal.command.set")) {
            this.messageService.send(sender, "noPermissions");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "set":
                if (args.length < 3) {
                    this.messageService.send(sender, "commandUsage");
                    return true;
                }

                Player target = this.server.getPlayer(args[1]);
                if (target == null) {
                    this.messageService.send(sender, "invalidPlayerProvided");
                    return true;
                }

                int health;
                try {
                    health = Integer.parseInt(args[2]);
                } catch (NumberFormatException exception) {
                    this.messageService.send(sender, "invalidHealthProvided");
                    return true;
                }

                this.adapter.setMaxHealth(target, health);

                String[] placeholders = { "{PLAYER}", target.getDisplayName(), "{HEALTH}", String.valueOf(health) };
                this.messageService.send(sender, "successfulCommandSet", placeholders);

                break;
            default:
                this.messageService.send(sender, "commandUsage");
        }

        return true;
    }

}
