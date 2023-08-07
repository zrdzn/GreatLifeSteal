package io.github.zrdzn.minecraft.greatlifesteal.elimination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import io.github.zrdzn.minecraft.greatlifesteal.PluginConfig;
import io.github.zrdzn.minecraft.greatlifesteal.user.UserNameCache;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class EliminationCommandCompleter implements TabCompleter {

    private final PluginConfig config;
    private final UserNameCache userNameCache;

    public EliminationCommandCompleter(PluginConfig config, UserNameCache userNameCache) {
        this.config = config;
        this.userNameCache = userNameCache;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Display all elimination keys.
        if (args.length == 1) {
            return new ArrayList<>(this.config.getEliminations().keySet());
        }

        // Display all online players.
        if (args.length == 2) {
            EliminationConfig elimination = this.config.getEliminations().get(args[1]);
            if (elimination == null) {
                return Collections.emptyList();
            }

            return new ArrayList<>(this.userNameCache.getUsersNames());
        }

        return Collections.emptyList();
    }

}
