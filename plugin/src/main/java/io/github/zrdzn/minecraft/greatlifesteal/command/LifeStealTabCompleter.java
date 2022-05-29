package io.github.zrdzn.minecraft.greatlifesteal.command;

import io.github.zrdzn.minecraft.greatlifesteal.config.PluginConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LifeStealTabCompleter implements TabCompleter {

    private final PluginConfig config;

    public LifeStealTabCompleter(PluginConfig config) {
        this.config = config;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return new ArrayList<String>() {{
                add("set");
                add("reload");
            }};
        }

        switch (args[0].toLowerCase()) {
            case "set":
                if (args.length == 2) {
                    List<String> players = new ArrayList<>();
                    Bukkit.getServer().getOnlinePlayers().forEach(player -> players.add(player.getName()));
                    return players;
                } else if (args.length == 3) {
                    List<String> health = new ArrayList<>();
                    health.add(String.valueOf(this.config.defaultHealth));
                    return health;
                }
            case "reload":
                return Collections.emptyList();
        }

        return Collections.emptyList();
    }

}
