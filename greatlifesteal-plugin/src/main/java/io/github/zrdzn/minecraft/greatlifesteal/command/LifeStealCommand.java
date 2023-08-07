package io.github.zrdzn.minecraft.greatlifesteal.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import io.github.zrdzn.minecraft.greatlifesteal.GreatLifeStealPlugin;
import io.github.zrdzn.minecraft.greatlifesteal.PluginConfig;
import io.github.zrdzn.minecraft.greatlifesteal.action.ActionConfig;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationConfig;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartDropLocation;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartItem;
import io.github.zrdzn.minecraft.greatlifesteal.message.MessageFacade;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.SpigotServer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LifeStealCommand implements CommandExecutor {

    private final Logger logger = LoggerFactory.getLogger(LifeStealCommand.class);

    private final GreatLifeStealPlugin plugin;
    private final PluginConfig config;
    private final DamageableAdapter adapter;
    private final HeartItem heartItem;
    private final SpigotServer spigotServer;
    private final Server server;

    public LifeStealCommand(GreatLifeStealPlugin plugin, PluginConfig config, DamageableAdapter adapter,
                            SpigotServer spigotServer, HeartItem heartItem) {
        this.plugin = plugin;
        this.config = config;
        this.adapter = adapter;
        this.heartItem = heartItem;
        this.spigotServer = spigotServer;
        this.server = plugin.getServer();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            MessageFacade.send(sender, this.config.getMessages().getCommandUsage());
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "health": {
                if (args.length < 2) {
                    MessageFacade.send(sender, this.config.getMessages().getCommandUsage());
                    return true;
                }

                switch (args[1].toLowerCase()) {
                    case "add": {
                        if (!sender.hasPermission("greatlifesteal.command.health.add")) {
                            MessageFacade.send(sender, this.config.getMessages().getNoPermissions());
                            return true;
                        }

                        if (args.length < 4) {
                            MessageFacade.send(sender, this.config.getMessages().getCommandUsage());
                            return true;
                        }

                        Player target = this.server.getPlayer(args[2]);
                        if (target == null) {
                            MessageFacade.send(sender, this.config.getMessages().getPlayerIsInvalid());
                            return true;
                        }

                        int health;
                        try {
                            health = Integer.parseInt(args[3]);
                        } catch (NumberFormatException exception) {
                            MessageFacade.send(sender, this.config.getMessages().getHealthIsInvalid());
                            return true;
                        }

                        double newHealth = this.adapter.getMaxHealth(target) + health;
                        // Check if the new health is lower or higher than configured limits
                        if (newHealth < this.config.getHealth().getMinimumHealth() || newHealth > this.config.getHealth().getMaximumHealth()) {
                            MessageFacade.send(sender, this.config.getMessages().getHealthIsInvalid());
                            return true;
                        }

                        this.adapter.setMaxHealth(target, newHealth);

                        String[] placeholders = { "{PLAYER}", target.getDisplayName(), "{HEALTH}", String.valueOf(health) };
                        MessageFacade.send(sender, this.config.getMessages().getHealthAdded(), placeholders);

                        break;
                    }
                    case "remove": {
                        if (!sender.hasPermission("greatlifesteal.command.health.remove")) {
                            MessageFacade.send(sender, this.config.getMessages().getNoPermissions());
                            return true;
                        }

                        if (args.length < 4) {
                            MessageFacade.send(sender, this.config.getMessages().getCommandUsage());
                            return true;
                        }

                        Player target = this.server.getPlayer(args[2]);
                        if (target == null) {
                            MessageFacade.send(sender, this.config.getMessages().getPlayerIsInvalid());
                            return true;
                        }

                        int health;
                        try {
                            health = Integer.parseInt(args[3]);
                        } catch (NumberFormatException exception) {
                            MessageFacade.send(sender, this.config.getMessages().getHealthIsInvalid());
                            return true;
                        }

                        double newHealth = this.adapter.getMaxHealth(target) - health;
                        // Check if the new health is lower or higher than configured limits
                        if (newHealth < this.config.getHealth().getMinimumHealth() || newHealth > this.config.getHealth().getMaximumHealth()) {
                            MessageFacade.send(sender, this.config.getMessages().getHealthIsInvalid());
                            return true;
                        }

                        this.adapter.setMaxHealth(target, newHealth);

                        String[] placeholders = { "{PLAYER}", target.getDisplayName(), "{HEALTH}", String.valueOf(health) };
                        MessageFacade.send(sender, this.config.getMessages().getHealthSubtracted(), placeholders);

                        break;
                    }
                    case "set": {
                        if (!sender.hasPermission("greatlifesteal.command.health.set")) {
                            MessageFacade.send(sender, this.config.getMessages().getNoPermissions());
                            return true;
                        }

                        if (args.length < 4) {
                            MessageFacade.send(sender, this.config.getMessages().getCommandUsage());
                            return true;
                        }

                        Player target = this.server.getPlayer(args[2]);
                        if (target == null) {
                            MessageFacade.send(sender, this.config.getMessages().getPlayerIsInvalid());
                            return true;
                        }

                        int health;
                        try {
                            health = Integer.parseInt(args[3]);
                        } catch (NumberFormatException exception) {
                            MessageFacade.send(sender, this.config.getMessages().getHealthIsInvalid());
                            return true;
                        }

                        // Check if the new health is lower or higher than configured limits
                        if (health < this.config.getHealth().getMinimumHealth() || health > this.config.getHealth().getMaximumHealth()) {
                            MessageFacade.send(sender, this.config.getMessages().getHealthIsInvalid());
                            return true;
                        }

                        this.adapter.setMaxHealth(target, health);

                        String[] placeholders = { "{PLAYER}", target.getDisplayName(), "{HEALTH}", String.valueOf(health) };
                        MessageFacade.send(sender, this.config.getMessages().getHealthSet(), placeholders);

                        break;
                    }
                    default:
                        MessageFacade.send(sender, this.config.getMessages().getCommandUsage());
                }

                break;
            }
            case "reload":
                if (!sender.hasPermission("greatlifesteal.command.reload")) {
                    MessageFacade.send(sender, this.config.getMessages().getNoPermissions());
                    return true;
                }

                this.plugin.loadConfigurations(this.config, this.spigotServer);
                MessageFacade.send(sender, this.config.getMessages().getPluginReloaded());

                break;
            case "lives": {
                if (args.length == 1) {
                    MessageFacade.send(sender, this.config.getMessages().getNoEliminationSpecified());
                    return true;
                }

                EliminationConfig elimination = this.config.getEliminations().get(args[1]);
                if (elimination == null) {
                    MessageFacade.send(sender, this.config.getMessages().getEliminationDoesNotExist());
                    return true;
                }

                Player target;
                if (args.length == 2) {
                    if (!sender.hasPermission("greatlifesteal.command.lives.self") && !sender.hasPermission("greatlifesteal.command.lives")) {
                        MessageFacade.send(sender, this.config.getMessages().getNoPermissions());
                        return true;
                    }

                    target = (Player) sender;
                } else {
                    if (!sender.hasPermission("greatlifesteal.command.lives")) {
                        MessageFacade.send(sender, this.config.getMessages().getNoPermissions());
                        return true;
                    }

                    target = this.server.getPlayer(args[2]);
                    if (target == null) {
                        MessageFacade.send(sender, this.config.getMessages().getPlayerIsInvalid());
                        return true;
                    }
                }

                double requiredHealth = elimination.getActivateAtHealth();
                double playerHealth = this.adapter.getMaxHealth(target);
                int lives = 0;

                if (playerHealth > requiredHealth) {
                    double healthChange = this.config.getHealth().getChange().getVictim();
                    lives = (int) Math.ceil((playerHealth - requiredHealth) / healthChange);
                }

                String[] placeholders = { "{PLAYER}", target.getName(), "{LIVES}", String.valueOf(lives) };
                MessageFacade.send(sender, this.config.getMessages().getPlayerLives(), placeholders);

                break;
            }
            case "withdraw": {
                if (args.length == 1) {
                    MessageFacade.send(sender, this.config.getMessages().getNoNumberSpecified());
                    return true;
                }

                int amount;
                try {
                    amount = Integer.parseUnsignedInt(args[1]);
                } catch (NumberFormatException exception) {
                    MessageFacade.send(sender, this.config.getMessages().getNumberIsInvalid());
                    return true;
                }

                if (amount <= 0) {
                    MessageFacade.send(sender, this.config.getMessages().getNumberMustBePositive());
                    return true;
                }

                Player target;
                if (args.length == 2) {
                    if (!sender.hasPermission("greatlifesteal.command.withdraw.self") && !sender.hasPermission("greatlifesteal.command.withdraw")) {
                        MessageFacade.send(sender, this.config.getMessages().getNoPermissions());
                        return true;
                    }

                    target = (Player) sender;
                } else {
                    if (!sender.hasPermission("greatlifesteal.command.withdraw")) {
                        MessageFacade.send(sender, this.config.getMessages().getNoPermissions());
                        return true;
                    }

                    target = this.server.getPlayer(args[2]);
                    if (target == null) {
                        MessageFacade.send(sender, this.config.getMessages().getPlayerIsInvalid());
                        return true;
                    }
                }

                double minimumHealth = this.config.getActions().values().stream()
                        .map(ActionConfig::getActivateAtHealth)
                        .max(Double::compareTo)
                        .orElse(this.config.getHealth().getMinimumHealth());

                double victimMaxHealth = this.adapter.getMaxHealth(target);
                double heartItemHealthAmount = amount * this.config.getHeart().getHealthAmount();
                double targetNewHealth = victimMaxHealth - heartItemHealthAmount;

                if (targetNewHealth <= minimumHealth) {
                    MessageFacade.send(sender, this.config.getMessages().getNotEnoughPlaceInInventory());
                    return true;
                }

                Inventory inventory = target.getInventory();

                List<ItemStack> heartsLeft = new ArrayList<>();
                for (int heartCount = 0; heartCount < amount; heartCount++) {
                    Map<Integer, ItemStack> heartsNotFitted = inventory.addItem(this.heartItem.getItemStack());
                    if (!heartsNotFitted.isEmpty() && this.config.getHeart().getDrop().getLocation() == HeartDropLocation.NONE) {
                        inventory.remove(this.heartItem.getItemStack());
                        MessageFacade.send(sender, this.config.getMessages().getNotEnoughPlaceInInventory());
                        return true;
                    }

                    heartsLeft.addAll(heartsNotFitted.values());
                }

                this.adapter.setMaxHealth(target, targetNewHealth);

                HeartDropLocation dropLocation = this.config.getHeart().getDrop().getLocationOnFullInventory();

                World world = target.getWorld();

                heartsLeft.forEach(item -> {
                    if (dropLocation == HeartDropLocation.GROUND_LEVEL) {
                        world.dropItemNaturally(target.getLocation(), item);
                    } else if (dropLocation == HeartDropLocation.EYE_LEVEL) {
                        world.dropItemNaturally(target.getEyeLocation(), item);
                    }
                });

                String[] placeholders = { "{PLAYER}", target.getName(), "{HEARTS}", String.valueOf(amount) };
                MessageFacade.send(sender, this.config.getMessages().getHeartsWithdraw(), placeholders);

                break;
            }
            default:
                MessageFacade.send(sender, this.config.getMessages().getCommandUsage());
        }

        return true;
    }

}
