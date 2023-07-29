package io.github.zrdzn.minecraft.greatlifesteal.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import io.github.zrdzn.minecraft.greatlifesteal.PluginConfig;
import io.github.zrdzn.minecraft.greatlifesteal.action.ActionConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class LifeStealTabCompleter implements TabCompleter {

    private final PluginConfig config;

    public LifeStealTabCompleter(PluginConfig config) {
        this.config = config;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return new ArrayList<String>() {
                {
                    boolean hasHealthPermission = sender.hasPermission("greatlifesteal.command.health.add") ||
                            sender.hasPermission("greatlifesteal.command.health.remove") ||
                            sender.hasPermission("greatlifesteal.command.health.set");
                    if (hasHealthPermission) {
                        this.add("health");
                    }

                    if (sender.hasPermission("greatlifesteal.command.reload")) {
                        this.add("reload");
                    }

                    if (sender.hasPermission("greatlifesteal.command.lives.self") ||
                            sender.hasPermission("greatlifesteal.command.lives")) {
                        this.add("lives");
                    }

                    if (sender.hasPermission("greatlifesteal.command.withdraw.self") ||
                            sender.hasPermission("greatlifesteal.command.withdraw")) {
                        this.add("withdraw");
                    }

                    if (sender.hasPermission("greatlifesteal.command.eliminate")) {
                        this.add("eliminate");
                    }

                    if (sender.hasPermission("greatlifesteal.command.revive")) {
                        this.add("revive");
                    }
                }
            };
        }

        double defaultHealth = this.config.getHealth().getDefaultMaximumHealth();

        switch (args[0].toLowerCase()) {
            case "health":
                if (args.length == 2) {
                    return new ArrayList<String>() {
                        {
                            if (sender.hasPermission("greatlifesteal.command.health.add")) {
                                this.add("add");
                            }

                            if (sender.hasPermission("greatlifesteal.command.health.remove")) {
                                this.add("remove");
                            }

                            if (sender.hasPermission("greatlifesteal.command.health.set")) {
                                this.add("set");
                            }
                        }
                    };
                } else if (args.length == 3) {
                    return Bukkit.getServer().getOnlinePlayers().stream()
                            .map(Player::getName)
                            .collect(Collectors.toList());
                } else if (args.length == 4) {
                    return Collections.singletonList(String.valueOf((int) defaultHealth));
                }
                break;
            case "lives":
            case "eliminate":
            case "revive":
                return this.getActionCompletion(args);
            case "withdraw":
                if (args.length == 2) {
                    return Collections.singletonList("1");
                } else if (args.length == 3) {
                    return Bukkit.getServer().getOnlinePlayers().stream()
                            .map(Player::getName)
                            .collect(Collectors.toList());
                }
                break;
            default:
                return Collections.emptyList();
        }

        return Collections.emptyList();
    }

    private List<String> getActionCompletion(String[] args) {
        if (args.length == 2) {
            return this.config.getActions().entrySet().stream()
                    .filter(action -> action.getValue().isEnabled())
                    .map(Entry::getKey)
                    .collect(Collectors.toList());
        } else if (args.length == 3) {
            ActionConfig action = this.config.getActions().get(args[1]);
            if (action == null || !action.isEnabled()) {
                return Collections.emptyList();
            }

            return Bukkit.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

}
