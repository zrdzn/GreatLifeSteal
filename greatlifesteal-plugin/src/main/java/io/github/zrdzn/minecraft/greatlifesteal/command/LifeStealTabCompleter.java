package io.github.zrdzn.minecraft.greatlifesteal.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import io.github.zrdzn.minecraft.greatlifesteal.PluginConfig;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationConfig;
import io.github.zrdzn.minecraft.greatlifesteal.user.UserNameCache;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class LifeStealTabCompleter implements TabCompleter {

    private final PluginConfig config;
    private final UserNameCache userNameCache;

    public LifeStealTabCompleter(PluginConfig config, UserNameCache userNameCache) {
        this.config = config;
        this.userNameCache = userNameCache;
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
                    return new ArrayList<>(this.userNameCache.getUsersNames());
                } else if (args.length == 4) {
                    return Collections.singletonList(String.valueOf((int) defaultHealth));
                }
                break;
            case "lives":
                return this.getEliminationCompletion(args);
            case "withdraw":
                if (args.length == 2) {
                    return Collections.singletonList("1");
                } else if (args.length == 3) {
                    return new ArrayList<>(this.userNameCache.getUsersNames());
                }
                break;
            default:
                return Collections.emptyList();
        }

        return Collections.emptyList();
    }

    private List<String> getEliminationCompletion(String[] args) {
        if (args.length == 2) {
            return new ArrayList<>(this.config.getEliminations().keySet());
        } else if (args.length == 3) {
            EliminationConfig elimination = this.config.getEliminations().get(args[1]);
            if (elimination == null) {
                return Collections.emptyList();
            }

            return new ArrayList<>(this.userNameCache.getUsersNames());
        }

        return Collections.emptyList();
    }

}
