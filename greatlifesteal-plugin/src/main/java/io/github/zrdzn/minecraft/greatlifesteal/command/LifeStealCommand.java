package io.github.zrdzn.minecraft.greatlifesteal.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import io.github.zrdzn.minecraft.greatlifesteal.GreatLifeStealPlugin;
import io.github.zrdzn.minecraft.greatlifesteal.PluginConfig;
import io.github.zrdzn.minecraft.greatlifesteal.action.ActionType;
import io.github.zrdzn.minecraft.greatlifesteal.action.ActionConfig;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.Elimination;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationConfig;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationException;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationFacade;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.revive.ReviveConfig;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.revive.ReviveStatus;
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
    private final PluginConfig config;
    private final EliminationFacade eliminationFacade;
    private final DamageableAdapter adapter;
    private final HeartItem heartItem;
    private final SpigotServer spigotServer;
    private final Server server;
    private final BukkitScheduler scheduler;

    public LifeStealCommand(GreatLifeStealPlugin plugin, PluginConfig config, EliminationFacade eliminationFacade,
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
                    MessageFacade.send(sender, this.config.getMessages().getNoActionSpecified());
                    return true;
                }

                ActionConfig action = this.config.getActions().get(args[1]);
                if (action == null) {
                    MessageFacade.send(sender, this.config.getMessages().getNoActionEnabled());
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

                double requiredHealth = action.getActivateAtHealth();
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
            case "eliminate": {
                if (!sender.hasPermission("greatlifesteal.command.eliminate")) {
                    MessageFacade.send(sender, this.config.getMessages().getNoPermissions());
                    return true;
                }

                if (args.length == 1) {
                    MessageFacade.send(sender, this.config.getMessages().getNoActionSpecified());
                    return true;
                }

                String eliminationKey = args[1];

                EliminationConfig eliminationConfig = this.config.getEliminations().get(eliminationKey);
                if (eliminationConfig == null) {
                    MessageFacade.send(sender, this.config.getMessages().getNoActionEnabled());
                    return true;
                }

                if (args.length == 2) {
                    MessageFacade.send(sender, this.config.getMessages().getPlayerIsInvalid());
                    return true;
                }

                Player victim = this.server.getPlayer(args[2]);
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

                this.scheduler.runTaskAsynchronously(this.plugin, () -> {
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

                    this.scheduler.runTask(this.plugin, () ->
                            eliminationConfig.getCommands().forEach(eliminationCommand -> {
                                eliminationCommand = MessageFacade.formatPlaceholders(eliminationCommand, placeholders);
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), eliminationCommand);
                            })
                    );
                });

                break;
            }
            case "revive": {
                if (!sender.hasPermission("greatlifesteal.command.revive")) {
                    MessageFacade.send(sender, this.config.getMessages().getNoPermissions());
                    return true;
                }

                if (args.length == 1) {
                    MessageFacade.send(sender, this.config.getMessages().getNoActionSpecified());
                    return true;
                }

                String reviveKey = args[1];

                ReviveConfig revive = this.config.getRevives().get(reviveKey);
                if (revive == null) {
                    MessageFacade.send(sender, this.config.getMessages().getReviveDoesNotExist());
                    return true;
                }

                if (args.length == 2) {
                    MessageFacade.send(sender, this.config.getMessages().getPlayerIsInvalid());
                    return true;
                }

                String victimName = args[2];

                this.scheduler.runTaskAsynchronously(this.plugin, () -> {
                    try {
                        Optional<Elimination> eliminationMaybe = this.eliminationFacade.findEliminationByPlayerName(victimName);
                        if (!eliminationMaybe.isPresent()) {
                            MessageFacade.send(sender, this.config.getMessages().getPlayerIsNotEliminated(), "{PLAYER}", victimName);
                            return;
                        }

                        // Execute all revive-related commands but do not remove the elimination from the database yet.
                        boolean statusChanged = this.eliminationFacade.updateReviveByPlayerName(victimName, ReviveStatus.COMPLETED);
                        if (statusChanged) {
                            this.scheduler.runTask(this.plugin, () ->
                                    revive.getCommands().getInitial().forEach(reviveCommand -> {
                                        reviveCommand = MessageFacade.formatPlaceholders(reviveCommand, "{victim}", victimName);
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), reviveCommand);
                                    })
                            );

                            return;
                        }

                        MessageFacade.send(sender, this.config.getMessages().getPlayerIsAlreadyRevived(), "{PLAYER}", victimName);
                    } catch (EliminationException exception) {
                        this.logger.error("Could not find or update an elimination.", exception);
                        MessageFacade.send(sender, this.config.getMessages().getCouldNotRevive());
                    }
                });

                break;
            }
            default:
                MessageFacade.send(sender, this.config.getMessages().getCommandUsage());
        }

        return true;
    }

}
