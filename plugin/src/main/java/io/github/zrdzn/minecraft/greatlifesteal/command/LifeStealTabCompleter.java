package io.github.zrdzn.minecraft.greatlifesteal.command;

import ch.jalu.configme.SettingsManager;
import io.github.zrdzn.minecraft.greatlifesteal.config.bean.beans.ActionBean;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.BaseConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class LifeStealTabCompleter implements TabCompleter {

    private final SettingsManager config;

    public LifeStealTabCompleter(SettingsManager config) {
        this.config = config;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return new ArrayList<String>() {
                {
                    if (sender.hasPermission("greatlifesteal.command.set")) {
                        this.add("set");
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

        double defaultHealth = this.config.getProperty(BaseConfig.DEFAULT_HEALTH);

        switch (args[0].toLowerCase()) {
            case "set":
                if (args.length == 2) {
                    return Bukkit.getServer().getOnlinePlayers().stream()
                            .map(Player::getName)
                            .collect(Collectors.toList());
                } else if (args.length == 3) {
                    return Collections.singletonList(String.valueOf((int) defaultHealth));
                }
                break;
            case "lives":
                if (args.length == 2) {
                    return this.config.getProperty(BaseConfig.CUSTOM_ACTIONS).entrySet().stream()
                            .filter(action -> action.getValue().isEnabled())
                            .map(Entry::getKey)
                            .collect(Collectors.toList());
                } else if (args.length == 3) {
                    ActionBean action = this.config.getProperty(BaseConfig.CUSTOM_ACTIONS).get(args[1]);
                    if (action == null || !action.isEnabled()) {
                        return Collections.emptyList();
                    }

                    return Bukkit.getServer().getOnlinePlayers().stream()
                            .map(Player::getName)
                            .collect(Collectors.toList());
                }
                break;
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

}
