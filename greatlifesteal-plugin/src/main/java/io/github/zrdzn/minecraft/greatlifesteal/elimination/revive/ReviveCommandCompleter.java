package io.github.zrdzn.minecraft.greatlifesteal.elimination.revive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import io.github.zrdzn.minecraft.greatlifesteal.PluginConfig;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationConfig;
import io.github.zrdzn.minecraft.greatlifesteal.user.UserNameCache;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class ReviveCommandCompleter implements TabCompleter {

    private final PluginConfig config;
    private final UserNameCache userNameCache;

    public ReviveCommandCompleter(PluginConfig config, UserNameCache userNameCache) {
        this.config = config;
        this.userNameCache = userNameCache;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Display all revive keys.
        if (args.length == 1) {
            return new ArrayList<>(this.config.getRevives().keySet());
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
