package io.github.zrdzn.minecraft.greatlifesteal.command;

import ch.jalu.configme.SettingsManager;
import io.github.zrdzn.minecraft.greatlifesteal.GreatLifeStealPlugin;
import io.github.zrdzn.minecraft.greatlifesteal.action.ActionType;
import io.github.zrdzn.minecraft.greatlifesteal.config.bean.ActionBean;
import io.github.zrdzn.minecraft.greatlifesteal.config.BaseConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.HealthChangeConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.MessagesConfig;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartConfig;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartDropConfig;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.Elimination;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationReviveStatus;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationService;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartDropLocation;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartItem;
import io.github.zrdzn.minecraft.greatlifesteal.message.MessageService;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.SpigotServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.slf4j.Logger;
import panda.std.Result;

public class LifeStealCommand implements CommandExecutor {

    private final GreatLifeStealPlugin plugin;
    private final Logger logger;
    private final SettingsManager config;
    private final EliminationService eliminationService;
    private final DamageableAdapter adapter;
    private final HeartItem heartItem;
    private final SpigotServer spigotServer;
    private final Server server;

    public LifeStealCommand(GreatLifeStealPlugin plugin, Logger logger, SettingsManager config, EliminationService eliminationService,
                            DamageableAdapter adapter, SpigotServer spigotServer, HeartItem heartItem) {
        this.plugin = plugin;
        this.logger = logger;
        this.config = config;
        this.eliminationService = eliminationService;
        this.adapter = adapter;
        this.heartItem = heartItem;
        this.spigotServer = spigotServer;
        this.server = plugin.getServer();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            MessageService.send(sender, this.config.getProperty(MessagesConfig.COMMAND_USAGE));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "health": {
                if (args.length < 2) {
                    MessageService.send(sender, this.config.getProperty(MessagesConfig.COMMAND_USAGE));
                    return true;
                }

                switch (args[1].toLowerCase()) {
                    case "add": {
                        if (!sender.hasPermission("greatlifesteal.command.health.add")) {
                            MessageService.send(sender, this.config.getProperty(MessagesConfig.NO_PERMISSIONS));
                            return true;
                        }
        
                        if (args.length < 4) {
                            MessageService.send(sender, this.config.getProperty(MessagesConfig.COMMAND_USAGE));
                            return true;
                        }
        
                        Player target = this.server.getPlayer(args[2]);
                        if (target == null) {
                            MessageService.send(sender, this.config.getProperty(MessagesConfig.INVALID_PLAYER_PROVIDED));
                            return true;
                        }
        
                        int health;
                        try {
                            health = Integer.parseInt(args[3]);
                        } catch (NumberFormatException exception) {
                            MessageService.send(sender, this.config.getProperty(MessagesConfig.INVALID_HEALTH_PROVIDED));
                            return true;
                        }
        
                        double newHealth = this.adapter.getMaxHealth(target) + health;
                        if (newHealth < this.config.getProperty(BaseConfig.MINIMUM_HEALTH) ||
                                newHealth > this.config.getProperty(BaseConfig.MAXIMUM_HEALTH)) {
                            MessageService.send(sender, this.config.getProperty(MessagesConfig.INVALID_HEALTH_PROVIDED));
                            return true;
                        }
        
                        this.adapter.setMaxHealth(target, newHealth);
        
                        String[] placeholders = { "{PLAYER}", target.getDisplayName(), "{HEALTH}", String.valueOf(health) };
                        MessageService.send(sender, this.config.getProperty(MessagesConfig.SUCCESSFUL_COMMAND_ADD), placeholders);
        
                        break;
                    }
                    case "remove": {
                        if (!sender.hasPermission("greatlifesteal.command.health.remove")) {
                            MessageService.send(sender, this.config.getProperty(MessagesConfig.NO_PERMISSIONS));
                            return true;
                        }
        
                        if (args.length < 4) {
                            MessageService.send(sender, this.config.getProperty(MessagesConfig.COMMAND_USAGE));
                            return true;
                        }
        
                        Player target = this.server.getPlayer(args[2]);
                        if (target == null) {
                            MessageService.send(sender, this.config.getProperty(MessagesConfig.INVALID_PLAYER_PROVIDED));
                            return true;
                        }
        
                        int health;
                        try {
                            health = Integer.parseInt(args[3]);
                        } catch (NumberFormatException exception) {
                            MessageService.send(sender, this.config.getProperty(MessagesConfig.INVALID_HEALTH_PROVIDED));
                            return true;
                        }
        
                        double newHealth = this.adapter.getMaxHealth(target) - health;
                        if (newHealth < this.config.getProperty(BaseConfig.MINIMUM_HEALTH) ||
                                newHealth > this.config.getProperty(BaseConfig.MAXIMUM_HEALTH)) {
                            MessageService.send(sender, this.config.getProperty(MessagesConfig.INVALID_HEALTH_PROVIDED));
                            return true;
                        }
        
                        this.adapter.setMaxHealth(target, newHealth);
        
                        String[] placeholders = { "{PLAYER}", target.getDisplayName(), "{HEALTH}", String.valueOf(health) };
                        MessageService.send(sender, this.config.getProperty(MessagesConfig.SUCCESSFUL_COMMAND_REMOVE), placeholders);
        
                        break;
                    }
                    case "set": {
                        if (!sender.hasPermission("greatlifesteal.command.health.set")) {
                            MessageService.send(sender, this.config.getProperty(MessagesConfig.NO_PERMISSIONS));
                            return true;
                        }
        
                        if (args.length < 4) {
                            MessageService.send(sender, this.config.getProperty(MessagesConfig.COMMAND_USAGE));
                            return true;
                        }
        
                        Player target = this.server.getPlayer(args[2]);
                        if (target == null) {
                            MessageService.send(sender, this.config.getProperty(MessagesConfig.INVALID_PLAYER_PROVIDED));
                            return true;
                        }
        
                        int health;
                        try {
                            health = Integer.parseInt(args[3]);
                        } catch (NumberFormatException exception) {
                            MessageService.send(sender, this.config.getProperty(MessagesConfig.INVALID_HEALTH_PROVIDED));
                            return true;
                        }
        
                        if (health < this.config.getProperty(BaseConfig.MINIMUM_HEALTH) ||
                                health > this.config.getProperty(BaseConfig.MAXIMUM_HEALTH)) {
                            MessageService.send(sender, this.config.getProperty(MessagesConfig.INVALID_HEALTH_PROVIDED));
                            return true;
                        }
        
                        this.adapter.setMaxHealth(target, health);
        
                        String[] placeholders = { "{PLAYER}", target.getDisplayName(), "{HEALTH}", String.valueOf(health) };
                        MessageService.send(sender, this.config.getProperty(MessagesConfig.SUCCESSFUL_COMMAND_SET), placeholders);
        
                        break;
                    }
                    default:
                        MessageService.send(sender, this.config.getProperty(MessagesConfig.COMMAND_USAGE));
                }

                break;
            }
            case "reload":
                if (!sender.hasPermission("greatlifesteal.command.reload")) {
                    MessageService.send(sender, this.config.getProperty(MessagesConfig.NO_PERMISSIONS));
                    return true;
                }

                if (this.plugin.loadConfigurations(this.config, this.logger, this.spigotServer, this.server)) {
                    MessageService.send(sender, this.config.getProperty(MessagesConfig.SUCCESSFUL_COMMAND_RELOAD));
                    return true;
                }

                MessageService.send(sender, this.config.getProperty(MessagesConfig.FAIL_COMMAND_RELOAD));

                break;
            case "lives": {
                if (args.length == 1) {
                    MessageService.send(sender, this.config.getProperty(MessagesConfig.NO_ACTION_SPECIFIED));
                    return true;
                }

                ActionBean action = this.config.getProperty(BaseConfig.CUSTOM_ACTIONS).get(args[1]);
                if (action == null || !action.isEnabled()) {
                    MessageService.send(sender, this.config.getProperty(MessagesConfig.NO_ACTION_ENABLED));
                    return true;
                }

                Player target;
                if (args.length == 2) {
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

                    target = this.server.getPlayer(args[2]);
                    if (target == null) {
                        MessageService.send(sender, this.config.getProperty(MessagesConfig.INVALID_PLAYER_PROVIDED));
                        return true;
                    }
                }

                double requiredHealth = action.getActivateAtHealth();
                double playerHealth = this.adapter.getMaxHealth(target);
                int lives = 0;

                if (playerHealth > requiredHealth) {
                    double healthChange = this.config.getProperty(HealthChangeConfig.VICTIM);
                    lives = (int) Math.ceil((playerHealth - requiredHealth) / healthChange);
                }

                String[] placeholders = { "{PLAYER}", target.getName(), "{LIVES}", String.valueOf(lives) };
                MessageService.send(sender, this.config.getProperty(MessagesConfig.SUCCESSFUL_COMMAND_LIVES), placeholders);

                break;
            }
            case "withdraw": {
                if (args.length == 1) {
                    MessageService.send(sender, this.config.getProperty(MessagesConfig.NO_NUMBER_SPECIFIED));
                    return true;
                }

                int amount;
                try {
                    amount = Integer.parseUnsignedInt(args[1]);
                } catch (NumberFormatException exception) {
                    MessageService.send(sender, this.config.getProperty(MessagesConfig.INVALID_NUMBER_PROVIDED));
                    return true;
                }

                if (amount <= 0) {
                    MessageService.send(sender, this.config.getProperty(MessagesConfig.POSITIVE_NUMBER_REQUIRED));
                    return true;
                }

                Player target;
                if (args.length == 2) {
                    if (!sender.hasPermission("greatlifesteal.command.withdraw.self") &&
                            !sender.hasPermission("greatlifesteal.command.withdraw")) {
                        MessageService.send(sender, this.config.getProperty(MessagesConfig.NO_PERMISSIONS));
                        return true;
                    }

                    target = (Player) sender;
                } else {
                    if (!sender.hasPermission("greatlifesteal.command.withdraw")) {
                        MessageService.send(sender, this.config.getProperty(MessagesConfig.NO_PERMISSIONS));
                        return true;
                    }

                    target = this.server.getPlayer(args[2]);
                    if (target == null) {
                        MessageService.send(sender, this.config.getProperty(MessagesConfig.INVALID_PLAYER_PROVIDED));
                        return true;
                    }
                }

                double minimumHealth = this.config.getProperty(BaseConfig.CUSTOM_ACTIONS).values().stream()
                        .map(ActionBean::getActivateAtHealth)
                        .max(Double::compareTo)
                        .orElse(this.config.getProperty(BaseConfig.MINIMUM_HEALTH));

                double victimMaxHealth = this.adapter.getMaxHealth(target);
                double heartItemHealthAmount = amount * this.config.getProperty(HeartConfig.HEALTH_AMOUNT);
                double targetNewHealth = victimMaxHealth - heartItemHealthAmount;

                if (targetNewHealth <= minimumHealth) {
                    MessageService.send(sender, this.config.getProperty(MessagesConfig.NOT_ENOUGH_HEALTH_WITHDRAW));
                    return true;
                }

                Inventory inventory = target.getInventory();

                List<ItemStack> heartsLeft = new ArrayList<>();
                for (int heartCount = 0; heartCount < amount; heartCount++) {
                    Map<Integer, ItemStack> heartsNotFitted = inventory.addItem(this.heartItem.getItemStack());
                    if (!heartsNotFitted.isEmpty() && this.config.getProperty(HeartDropConfig.FULL_INVENTORY_LOCATION) == HeartDropLocation.NONE) {
                        inventory.remove(this.heartItem.getItemStack());
                        MessageService.send(sender, this.config.getProperty(MessagesConfig.NOT_ENOUGH_PLACE_INVENTORY));
                        return true;
                    }

                    heartsLeft.addAll(heartsNotFitted.values());
                }

                this.adapter.setMaxHealth(target, targetNewHealth);

                HeartDropLocation dropLocation = this.config.getProperty(HeartDropConfig.FULL_INVENTORY_LOCATION);

                World world = target.getWorld();

                heartsLeft.forEach(item -> {
                    if (dropLocation == HeartDropLocation.GROUND_LEVEL)  {
                        world.dropItemNaturally(target.getLocation(), item);
                    } else if (dropLocation == HeartDropLocation.EYE_LEVEL) {
                        world.dropItemNaturally(target.getEyeLocation(), item);
                    }
                });

                String[] placeholders = { "{PLAYER}", target.getName(), "{HEARTS}", String.valueOf(amount) };
                MessageService.send(sender, this.config.getProperty(MessagesConfig.SUCCESSFUL_COMMAND_WITHDRAW), placeholders);

                break;
            }
            case "eliminate": {
                if (!sender.hasPermission("greatlifesteal.command.eliminate")) {
                    MessageService.send(sender, this.config.getProperty(MessagesConfig.NO_PERMISSIONS));
                    return true;
                }

                if (args.length == 1) {
                    MessageService.send(sender, this.config.getProperty(MessagesConfig.NO_ACTION_SPECIFIED));
                    return true;
                }

                ActionBean action = this.config.getProperty(BaseConfig.CUSTOM_ACTIONS).get(args[1]);
                if (action == null || !action.isEnabled()) {
                    MessageService.send(sender, this.config.getProperty(MessagesConfig.NO_ACTION_ENABLED));
                    return true;
                }

                ActionType actionType = action.getType();

                boolean allowedAction = actionType == ActionType.BAN || actionType == ActionType.DISPATCH_COMMANDS;
                if (!allowedAction) {
                    MessageService.send(sender, this.config.getProperty(MessagesConfig.ACTION_TYPE_INVALID));
                    return true;
                }

                if (args.length == 2) {
                    MessageService.send(sender, this.config.getProperty(MessagesConfig.INVALID_PLAYER_PROVIDED));
                    return true;
                }

                Player victim = this.server.getPlayer(args[2]);
                if (victim == null) {
                    MessageService.send(sender, this.config.getProperty(MessagesConfig.INVALID_PLAYER_PROVIDED));
                    return true;
                }

                String victimName = victim.getName();

                int senderHealth = 0;
                if (sender instanceof Player) {
                    senderHealth = (int) ((Player) sender).getMaxHealth();
                }

                String[] placeholders = {
                        "{player}", victimName,
                        "{victim}", victimName,
                        "{killer}", sender.getName(),
                        "{victim_max_health}", String.valueOf((int) victim.getMaxHealth()),
                        "{killer_max_health}", String.valueOf(senderHealth),
                };

                Elimination elimination = new Elimination();
                elimination.setCreatedAt(Instant.now());
                elimination.setPlayerUuid(victim.getUniqueId());
                elimination.setPlayerName(victimName);
                elimination.setAction(args[1]);

                Result<Optional<Elimination>, Exception> foundElimination = this.eliminationService.getElimination(elimination.getPlayerUuid()).join();

                foundElimination
                        .peek(eliminationMaybe -> {
                            if (eliminationMaybe.isPresent()) {
                                MessageService.send(sender, this.config.getProperty(MessagesConfig.ELIMINATION_PRESENT),
                                        "{PLAYER}", victim.getName());
                                return;
                            }

                            Result<Elimination, Exception> createResult = this.eliminationService.createElimination(elimination).join();

                            createResult
                                    .peek(ignored -> {
                                        // Kick user if the action type is a ban.
                                        if (actionType == ActionType.BAN) {
                                            victim.kickPlayer(ChatColor.translateAlternateColorCodes('&', String.join("\n", action.getParameters())));
                                            return;
                                        }

                                        action.getParameters().forEach(parameter -> {
                                            parameter = MessageService.formatPlaceholders(parameter, placeholders);

                                            // Dispatch custom commands for the elimination.
                                            if (Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parameter)) {
                                                this.eliminationService.removeElimination(victim.getUniqueId()).join();
                                            }
                                        });
                                    })
                                    .onError(error -> {
                                        this.logger.error("Could not eliminate a player via command.", error);
                                        MessageService.send(sender, this.config.getProperty(MessagesConfig.FAIL_COMMAND_ELIMINATE));
                                    });
                        })
                        .onError(error -> {
                            this.logger.error("Could not eliminate a player via command.", error);
                            MessageService.send(sender, this.config.getProperty(MessagesConfig.FAIL_COMMAND_ELIMINATE));
                        });

                break;
            }
            case "revive": {
                if (!sender.hasPermission("greatlifesteal.command.revive")) {
                    MessageService.send(sender, this.config.getProperty(MessagesConfig.NO_PERMISSIONS));
                    return true;
                }

                if (args.length == 1) {
                    MessageService.send(sender, this.config.getProperty(MessagesConfig.NO_ACTION_SPECIFIED));
                    return true;
                }

                ActionBean action = this.config.getProperty(BaseConfig.CUSTOM_ACTIONS).get(args[1]);
                if (action == null || !action.isEnabled()) {
                    MessageService.send(sender, this.config.getProperty(MessagesConfig.NO_ACTION_ENABLED));
                    return true;
                }

                ActionType actionType = action.getType();

                boolean allowedAction = actionType == ActionType.BAN || actionType == ActionType.DISPATCH_COMMANDS;
                if (!allowedAction) {
                    MessageService.send(sender, this.config.getProperty(MessagesConfig.ACTION_TYPE_INVALID));
                    return true;
                }


                if (args.length == 2) {
                    MessageService.send(sender, this.config.getProperty(MessagesConfig.INVALID_PLAYER_PROVIDED));
                    return true;
                }

                String victimName = args[2];

                this.eliminationService.getElimination(victimName).thenAccept(result -> result
                        .peek(eliminationMaybe -> {
                            if (!eliminationMaybe.isPresent()) {
                                MessageService.send(sender, this.config.getProperty(MessagesConfig.NO_ELIMINATION_PRESENT),
                                        "{PLAYER}", victimName);
                                return;
                            }

                            // Execute all revive-related commands but do not remove the elimination from the database yet.
                            this.eliminationService.changeReviveStatus(victimName, EliminationReviveStatus.COMPLETED).join()
                                    .peek(success -> {
                                        if (success) {
                                            if (actionType == ActionType.DISPATCH_COMMANDS) {
                                                action.getRevive().getCommands().forEach(parameter -> {
                                                    parameter = MessageService.formatPlaceholders(parameter, "{victim}", victimName);
                                                    if (Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parameter)) {
                                                        this.eliminationService.changeReviveStatus(victimName, EliminationReviveStatus.PENDING).join();
                                                    }
                                                });
                                            }
                                        }
                                    })
                                    .onError(error -> {
                                        this.logger.error("Could not change a revive status.", error);
                                        MessageService.send(sender, this.config.getProperty(MessagesConfig.FAIL_COMMAND_ELIMINATE));
                                    });
                        })
                        .onError(error -> {
                            this.logger.error("Could not revive a player via command.", error);
                            MessageService.send(sender, this.config.getProperty(MessagesConfig.FAIL_COMMAND_ELIMINATE));
                        }));

                break;
            }
            default:
                MessageService.send(sender, this.config.getProperty(MessagesConfig.COMMAND_USAGE));
        }

        return true;
    }

}
