package io.github.zrdzn.minecraft.greatlifesteal.command;

import io.github.zrdzn.minecraft.greatlifesteal.configs.BaseSettingsConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class LifeStealTabCompleter implements TabCompleter {

    private final BaseSettingsConfig config;

    public LifeStealTabCompleter(BaseSettingsConfig config) {
        this.config = config;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        boolean eliminationEnabled = this.config.eliminationMode.enabled;
        if (args.length == 1) {
            return new ArrayList<String>() {
                {
                    if (eliminationEnabled) {
                        this.add("lives");
                    }

                    this.add("set");
                    this.add("reload");
                }
            };
        }

        switch (args[0].toLowerCase()) {
            case "set":
                if (args.length == 2) {
                    List<String> players = new ArrayList<>();
                    Bukkit.getServer().getOnlinePlayers().forEach(player -> players.add(player.getName()));
                    return players;
                } else if (args.length == 3) {
                    return Collections.singletonList(String.valueOf(this.config.defaultHealth));
                }
                break;
            case "lives":
                if (args.length == 2 && eliminationEnabled) {
                    List<String> players = new ArrayList<>();
                    Bukkit.getServer().getOnlinePlayers().forEach(player -> players.add(player.getName()));
                    return players;
                }
                break;
            default:
                return Collections.emptyList();
        }

        return Collections.emptyList();
    }

}
