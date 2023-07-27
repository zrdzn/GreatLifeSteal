package io.github.zrdzn.minecraft.greatlifesteal.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import ch.jalu.configme.SettingsManager;
import io.github.zrdzn.minecraft.greatlifesteal.GreatLifeStealPlugin;
import io.github.zrdzn.minecraft.greatlifesteal.action.ActionType;
import io.github.zrdzn.minecraft.greatlifesteal.config.BaseConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.HealthChangeConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.MessagesConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.bean.ActionBean;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.Elimination;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationException;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationFacade;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationReviveStatus;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartConfig;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartDropConfig;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartDropLocation;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartItem;
import io.github.zrdzn.minecraft.greatlifesteal.message.MessageFacade;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
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
import org.bukkit.scheduler.BukkitScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LifeStealCommand implements CommandExecutor {

    private final Logger logger = LoggerFactory.getLogger(LifeStealCommand.class);

    private final GreatLifeStealPlugin plugin;
    private final SettingsManager config;
    private final EliminationFacade eliminationFacade;
    private final DamageableAdapter adapter;
    private final HeartItem heartItem;
    private final SpigotServer spigotServer;
    private final Server server;
    private final BukkitScheduler scheduler;

    public LifeStealCommand(GreatLifeStealPlugin plugin, SettingsManager config, EliminationFacade eliminationFacade,
                            DamageableAdapter adapter, SpigotServer spigotServer, HeartItem heartItem) {
        this.plugin = plugin;
        this.config = config;
        this.eliminationFacade = eliminationFacade;
        this.adapter = adapter;
        this.heartItem = heartItem;
        this.spigotServer = spigotServer;
        this.server = plugin.getServer();
        this.scheduler = this.server.getScheduler();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            MessageFacade.send(sender, this.config.getProperty(MessagesConfig.COMMAND_USAGE));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "health": {
                if (args.length < 2) {
                    MessageFacade.send(sender, this.config.getProperty(MessagesConfig.COMMAND_USAGE));
                    return true;
                }

                switch (args[1].toLowerCase()) {
                    case "add": {
                        if (!sender.hasPermission("greatlifesteal.command.health.add")) {
                            MessageFacade.send(sender, this.config.getProperty(MessagesConfig.NO_PERMISSIONS));
                            return true;
                        }

                        if (args.length < 4) {
                            MessageFacade.send(sender, this.config.getProperty(MessagesConfig.COMMAND_USAGE));
                            return true;
                        }

                        Player target = this.server.getPlayer(args[2]);
                        if (target == null) {
                            MessageFacade.send(sender, this.config.getProperty(MessagesConfig.INVALID_PLAYER_PROVIDED));
                            return true;
                        }

                        int health;
                        try {
                            health = Integer.parseInt(args[3]);
                        } catch (NumberFormatException exception) {
                            MessageFacade.send(sender, this.config.getProperty(MessagesConfig.INVALID_HEALTH_PROVIDED));
                            return true;
                        }

                        double newHealth = this.adapter.getMaxHealth(target) + health;
                        if (newHealth < this.config.getProperty(BaseConfig.MINIMUM_HEALTH) ||
                                newHealth > this.config.getProperty(BaseConfig.MAXIMUM_HEALTH)) {
                            MessageFacade.send(sender, this.config.getProperty(MessagesConfig.INVALID_HEALTH_PROVIDED));
                            return true;
                        }

                        this.adapter.setMaxHealth(target, newHealth);

                        String[] placeholders = { "{PLAYER}", target.getDisplayName(), "{HEALTH}", String.valueOf(health) };
                        MessageFacade.send(sender, this.config.getProperty(MessagesConfig.SUCCESSFUL_COMMAND_ADD), placeholders);

                        break;
                    }
                    case "remove": {
                        if (!sender.hasPermission("greatlifesteal.command.health.remove")) {
                            MessageFacade.send(sender, this.config.getProperty(MessagesConfig.NO_PERMISSIONS));
                            return true;
                        }

                        if (args.length < 4) {
                            MessageFacade.send(sender, this.config.getProperty(MessagesConfig.COMMAND_USAGE));
                            return true;
                        }

                        Player target = this.server.getPlayer(args[2]);
                        if (target == null) {
                            MessageFacade.send(sender, this.config.getProperty(MessagesConfig.INVALID_PLAYER_PROVIDED));
                            return true;
                        }

                        int health;
                        try {
                            health = Integer.parseInt(args[3]);
                        } catch (NumberFormatException exception) {
                            MessageFacade.send(sender, this.config.getProperty(MessagesConfig.INVALID_HEALTH_PROVIDED));
                            return true;
                        }

                        double newHealth = this.adapter.getMaxHealth(target) - health;
                        if (newHealth < this.config.getProperty(BaseConfig.MINIMUM_HEALTH) ||
                                newHealth > this.config.getProperty(BaseConfig.MAXIMUM_HEALTH)) {
                            MessageFacade.send(sender, this.config.getProperty(MessagesConfig.INVALID_HEALTH_PROVIDED));
                            return true;
                        }

                        this.adapter.setMaxHealth(target, newHealth);

                        String[] placeholders = { "{PLAYER}", target.getDisplayName(), "{HEALTH}", String.valueOf(health) };
                        MessageFacade.send(sender, this.config.getProperty(MessagesConfig.SUCCESSFUL_COMMAND_REMOVE), placeholders);

                        break;
                    }
                    case "set": {
                        if (!sender.hasPermission("greatlifesteal.command.health.set")) {
                            MessageFacade.send(sender, this.config.getProperty(MessagesConfig.NO_PERMISSIONS));
                            return true;
                        }

                        if (args.length < 4) {
                            MessageFacade.send(sender, this.config.getProperty(MessagesConfig.COMMAND_USAGE));
                            return true;
                        }

                        Player target = this.server.getPlayer(args[2]);
                        if (target == null) {
                            MessageFacade.send(sender, this.config.getProperty(MessagesConfig.INVALID_PLAYER_PROVIDED));
                            return true;
                        }

                        int health;
                        try {
                            health = Integer.parseInt(args[3]);
                        } catch (NumberFormatException exception) {
                            MessageFacade.send(sender, this.config.getProperty(MessagesConfig.INVALID_HEALTH_PROVIDED));
                            return true;
                        }

                        if (health < this.config.getProperty(BaseConfig.MINIMUM_HEALTH) ||
                                health > this.config.getProperty(BaseConfig.MAXIMUM_HEALTH)) {
                            MessageFacade.send(sender, this.config.getProperty(MessagesConfig.INVALID_HEALTH_PROVIDED));
                            return true;
                        }

                        this.adapter.setMaxHealth(target, health);

                        String[] placeholders = { "{PLAYER}", target.getDisplayName(), "{HEALTH}", String.valueOf(health) };
                        MessageFacade.send(sender, this.config.getProperty(MessagesConfig.SUCCESSFUL_COMMAND_SET), placeholders);

                        break;
                    }
                    default:
                        MessageFacade.send(sender, this.config.getProperty(MessagesConfig.COMMAND_USAGE));
                }

                break;
            }
            case "reload":
                if (!sender.hasPermission("greatlifesteal.command.reload")) {
                    MessageFacade.send(sender, this.config.getProperty(MessagesConfig.NO_PERMISSIONS));
                    return true;
                }

                this.plugin.loadConfigurations(this.config, this.spigotServer, this.server);
                MessageFacade.send(sender, this.config.getProperty(MessagesConfig.SUCCESSFUL_COMMAND_RELOAD));

                break;
            case "lives": {
                if (args.length == 1) {
                    MessageFacade.send(sender, this.config.getProperty(MessagesConfig.NO_ACTION_SPECIFIED));
                    return true;
                }

                ActionBean action = this.config.getProperty(BaseConfig.CUSTOM_ACTIONS).get(args[1]);
                if (action == null || !action.isEnabled()) {
                    MessageFacade.send(sender, this.config.getProperty(MessagesConfig.NO_ACTION_ENABLED));
                    return true;
                }

                Player target;
                if (args.length == 2) {
                    if (!sender.hasPermission("greatlifesteal.command.lives.self") &&
                            !sender.hasPermission("greatlifesteal.command.lives")) {
                        MessageFacade.send(sender, this.config.getProperty(MessagesConfig.NO_PERMISSIONS));
                        return true;
                    }

                    target = (Player) sender;
                } else {
                    if (!sender.hasPermission("greatlifesteal.command.lives")) {
                        MessageFacade.send(sender, this.config.getProperty(MessagesConfig.NO_PERMISSIONS));
                        return true;
                    }

                    target = this.server.getPlayer(args[2]);
                    if (target == null) {
                        MessageFacade.send(sender, this.config.getProperty(MessagesConfig.INVALID_PLAYER_PROVIDED));
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
                MessageFacade.send(sender, this.config.getProperty(MessagesConfig.SUCCESSFUL_COMMAND_LIVES), placeholders);

                break;
            }
            case "withdraw": {
                if (args.length == 1) {
                    MessageFacade.send(sender, this.config.getProperty(MessagesConfig.NO_NUMBER_SPECIFIED));
                    return true;
                }

                int amount;
                try {
                    amount = Integer.parseUnsignedInt(args[1]);
                } catch (NumberFormatException exception) {
                    MessageFacade.send(sender, this.config.getProperty(MessagesConfig.INVALID_NUMBER_PROVIDED));
                    return true;
                }

                if (amount <= 0) {
                    MessageFacade.send(sender, this.config.getProperty(MessagesConfig.POSITIVE_NUMBER_REQUIRED));
                    return true;
                }

                Player target;
                if (args.length == 2) {
                    if (!sender.hasPermission("greatlifesteal.command.withdraw.self") &&
                            !sender.hasPermission("greatlifesteal.command.withdraw")) {
                        MessageFacade.send(sender, this.config.getProperty(MessagesConfig.NO_PERMISSIONS));
                        return true;
                    }

                    target = (Player) sender;
                } else {
                    if (!sender.hasPermission("greatlifesteal.command.withdraw")) {
                        MessageFacade.send(sender, this.config.getProperty(MessagesConfig.NO_PERMISSIONS));
                        return true;
                    }

                    target = this.server.getPlayer(args[2]);
                    if (target == null) {
                        MessageFacade.send(sender, this.config.getProperty(MessagesConfig.INVALID_PLAYER_PROVIDED));
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
                    MessageFacade.send(sender, this.config.getProperty(MessagesConfig.NOT_ENOUGH_HEALTH_WITHDRAW));
                    return true;
                }

                Inventory inventory = target.getInventory();

                List<ItemStack> heartsLeft = new ArrayList<>();
                for (int heartCount = 0; heartCount < amount; heartCount++) {
                    Map<Integer, ItemStack> heartsNotFitted = inventory.addItem(this.heartItem.getItemStack());
                    if (!heartsNotFitted.isEmpty() && this.config.getProperty(HeartDropConfig.FULL_INVENTORY_LOCATION) == HeartDropLocation.NONE) {
                        inventory.remove(this.heartItem.getItemStack());
                        MessageFacade.send(sender, this.config.getProperty(MessagesConfig.NOT_ENOUGH_PLACE_INVENTORY));
                        return true;
                    }

                    heartsLeft.addAll(heartsNotFitted.values());
                }

                this.adapter.setMaxHealth(target, targetNewHealth);

                HeartDropLocation dropLocation = this.config.getProperty(HeartDropConfig.FULL_INVENTORY_LOCATION);

                World world = target.getWorld();

                heartsLeft.forEach(item -> {
                    if (dropLocation == HeartDropLocation.GROUND_LEVEL) {
                        world.dropItemNaturally(target.getLocation(), item);
                    } else if (dropLocation == HeartDropLocation.EYE_LEVEL) {
                        world.dropItemNaturally(target.getEyeLocation(), item);
                    }
                });

                String[] placeholders = { "{PLAYER}", target.getName(), "{HEARTS}", String.valueOf(amount) };
                MessageFacade.send(sender, this.config.getProperty(MessagesConfig.SUCCESSFUL_COMMAND_WITHDRAW), placeholders);

                break;
            }
            case "eliminate": {
                if (!sender.hasPermission("greatlifesteal.command.eliminate")) {
                    MessageFacade.send(sender, this.config.getProperty(MessagesConfig.NO_PERMISSIONS));
                    return true;
                }

                if (args.length == 1) {
                    MessageFacade.send(sender, this.config.getProperty(MessagesConfig.NO_ACTION_SPECIFIED));
                    return true;
                }

                String actionKey = args[1];

                ActionBean action = this.config.getProperty(BaseConfig.CUSTOM_ACTIONS).get(actionKey);
                if (action == null || !action.isEnabled()) {
                    MessageFacade.send(sender, this.config.getProperty(MessagesConfig.NO_ACTION_ENABLED));
                    return true;
                }

                ActionType actionType = action.getType();

                boolean allowedAction = actionType == ActionType.BAN || actionType == ActionType.DISPATCH_COMMANDS;
                if (!allowedAction) {
                    MessageFacade.send(sender, this.config.getProperty(MessagesConfig.ACTION_TYPE_INVALID));
                    return true;
                }

                if (args.length == 2) {
                    MessageFacade.send(sender, this.config.getProperty(MessagesConfig.INVALID_PLAYER_PROVIDED));
                    return true;
                }

                Player victim = this.server.getPlayer(args[2]);
                if (victim == null) {
                    MessageFacade.send(sender, this.config.getProperty(MessagesConfig.INVALID_PLAYER_PROVIDED));
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

                this.scheduler.runTaskAsynchronously(this.plugin, () -> {
                    try {
                        Optional<Elimination> eliminationMaybe = this.eliminationFacade.findEliminationByPlayerUuid(victim.getUniqueId());
                        if (eliminationMaybe.isPresent()) {
                            MessageFacade.send(sender, this.config.getProperty(MessagesConfig.ELIMINATION_PRESENT), "{PLAYER}", victim.getName());
                            return;
                        }

                        this.eliminationFacade.createElimination(victim.getUniqueId(), victimName, actionKey, victim.getWorld().getName());
                    } catch (EliminationException exception) {
                        this.logger.error("Could not find or create an elimination.", exception);
                        MessageFacade.send(sender, this.config.getProperty(MessagesConfig.FAIL_COMMAND_ELIMINATE));
                        return;
                    }

                    this.scheduler.runTask(this.plugin, () -> {
                        if (actionType == ActionType.BAN) {
                            victim.kickPlayer(ChatColor.translateAlternateColorCodes('&', String.join("\n", action.getParameters())));
                            return;
                        }

                        action.getParameters().forEach(parameter -> {
                            parameter = MessageFacade.formatPlaceholders(parameter, placeholders);

                            // Dispatch custom commands for the elimination.
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parameter);
                        });
                    });
                });

                break;
            }
            case "revive": {
                if (!sender.hasPermission("greatlifesteal.command.revive")) {
                    MessageFacade.send(sender, this.config.getProperty(MessagesConfig.NO_PERMISSIONS));
                    return true;
                }

                if (args.length == 1) {
                    MessageFacade.send(sender, this.config.getProperty(MessagesConfig.NO_ACTION_SPECIFIED));
                    return true;
                }

                String actionKey = args[1];

                ActionBean action = this.config.getProperty(BaseConfig.CUSTOM_ACTIONS).get(actionKey);
                if (action == null || !action.isEnabled()) {
                    MessageFacade.send(sender, this.config.getProperty(MessagesConfig.NO_ACTION_ENABLED));
                    return true;
                }

                ActionType actionType = action.getType();

                boolean allowedAction = actionType == ActionType.BAN || actionType == ActionType.DISPATCH_COMMANDS;
                if (!allowedAction) {
                    MessageFacade.send(sender, this.config.getProperty(MessagesConfig.ACTION_TYPE_INVALID));
                    return true;
                }


                if (args.length == 2) {
                    MessageFacade.send(sender, this.config.getProperty(MessagesConfig.INVALID_PLAYER_PROVIDED));
                    return true;
                }

                String victimName = args[2];

                this.scheduler.runTaskAsynchronously(this.plugin, () -> {
                    try {
                        Optional<Elimination> eliminationMaybe = this.eliminationFacade.findEliminationByPlayerName(victimName);
                        if (!eliminationMaybe.isPresent()) {
                            MessageFacade.send(sender, this.config.getProperty(MessagesConfig.NO_ELIMINATION_PRESENT), "{PLAYER}", victimName);
                            return;
                        }

                        // Execute all revive-related commands but do not remove the elimination from the database yet.
                        boolean statusChanged = this.eliminationFacade.updateReviveByPlayerName(victimName, EliminationReviveStatus.COMPLETED);
                        if (statusChanged) {
                            if (actionType == ActionType.DISPATCH_COMMANDS) {
                                this.scheduler.runTask(this.plugin, () ->
                                        action.getRevive().getCommands().forEach(parameter -> {
                                            parameter = MessageFacade.formatPlaceholders(parameter, "{victim}", victimName);
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parameter);
                                        })
                                );
                            }

                            return;
                        }

                        MessageFacade.send(sender, this.config.getProperty(MessagesConfig.PLAYER_IS_ALREADY_REVIVED), "{PLAYER}", victimName);
                    } catch (EliminationException exception) {
                        this.logger.error("Could not find or update an elimination.", exception);
                        MessageFacade.send(sender, this.config.getProperty(MessagesConfig.FAIL_COMMAND_REVIVE));
                    }
                });

                break;
            }
            default:
                MessageFacade.send(sender, this.config.getProperty(MessagesConfig.COMMAND_USAGE));
        }

        return true;
    }

}
